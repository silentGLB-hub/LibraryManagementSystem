package com.library.controller;

import com.library.dao.BorrowDAO;
import com.library.model.BorrowRecord;
import com.library.model.ChartPoint;
import com.library.service.SmtpEmailService;
import com.library.util.AuthUtil;
import com.library.util.ReportExportUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ReportController {

    @Autowired
    private BorrowDAO borrowDAO;

    @Autowired
    private SmtpEmailService emailService;

    @GetMapping("/reports")
    public String reports(Model model, HttpSession session) {
        if (!AuthUtil.canManageLibrary(session)) {
            return "redirect:/login";
        }
        model.addAttribute("records", borrowDAO.findAll());
        model.addAttribute("statusStats", borrowDAO.countByStatus());
        model.addAttribute("topBooks", borrowDAO.topBorrowedBooks(5));
        model.addAttribute("overdueRecords", borrowDAO.findOverdue());
        model.addAttribute("monthlyStats", borrowDAO.borrowCountByMonth());
        model.addAttribute("yearlyStats", borrowDAO.borrowCountByYear());
        model.addAttribute("readerActivity", borrowDAO.readerActivity(6));
        List<ChartPoint> monthlyChart = borrowDAO.monthlyBorrowReturnChart();
        model.addAttribute("monthlyChart", monthlyChart);
        model.addAttribute("monthlyLinePoints", linePoints(monthlyChart));
        model.addAttribute("smtpConfigured", emailService.isConfigured());
        return "reports";
    }

    @GetMapping("/reports/export/excel")
    public void exportExcel(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AuthUtil.canManageLibrary(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        response.setContentType("application/vnd.ms-excel; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=library-report.xls");
        ReportExportUtil.writeExcelHtml(borrowDAO.findAll(), response.getOutputStream());
    }

    @GetMapping("/reports/export/pdf")
    public void exportPdf(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!AuthUtil.canManageLibrary(session)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=library-report.pdf");
        ReportExportUtil.writePdf(borrowDAO.findAll(), response.getOutputStream());
    }

    @GetMapping("/reports/reminders")
    public String sendReminders(HttpSession session, RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Permission denied.");
                return "redirect:/reports";
            }
            if (!emailService.isConfigured()) {
                redirect.addFlashAttribute("warning", "SMTP is not configured, so overdue reminder emails were not sent.");
                return "redirect:/reports";
            }
            List<BorrowRecord> overdue = borrowDAO.findOverdue();
            if (overdue.isEmpty()) {
                redirect.addFlashAttribute("message", "There are no overdue records to remind.");
                return "redirect:/reports";
            }
            int sent = emailService.sendOverdueReminders(overdue);
            redirect.addFlashAttribute("message", "Sent " + sent + " overdue reminder email(s).");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Cannot send reminders: " + e.getMessage());
        }
        return "redirect:/reports";
    }

    private String linePoints(List<ChartPoint> points) {
        StringBuilder builder = new StringBuilder();
        for (ChartPoint point : points) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(point.getX()).append(',').append(point.getY());
        }
        return builder.toString();
    }
}

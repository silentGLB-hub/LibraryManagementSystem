package com.library.controller;

import com.library.dao.BorrowDAO;
import com.library.model.BorrowRecord;
import com.library.model.ChartPoint;
import com.library.model.StatItem;
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
        List<StatItem> statusStats = borrowDAO.countByStatus();
        List<StatItem> topBooks = borrowDAO.topBorrowedBooks(5);
        List<StatItem> yearlyStats = borrowDAO.borrowCountByYear();
        List<StatItem> readerActivity = borrowDAO.readerActivity(6);

        model.addAttribute("statusStats", statusStats);
        model.addAttribute("statusLinePoints", statLinePoints(statusStats));
        model.addAttribute("topBooks", topBooks);
        model.addAttribute("topBooksLinePoints", statLinePoints(topBooks));
        model.addAttribute("overdueRecords", borrowDAO.findOverdue());
        model.addAttribute("monthlyStats", borrowDAO.borrowCountByMonth());
        model.addAttribute("yearlyStats", yearlyStats);
        model.addAttribute("yearlyLinePoints", statLinePoints(yearlyStats));
        model.addAttribute("readerActivity", readerActivity);
        model.addAttribute("readerActivityLinePoints", statLinePoints(readerActivity));
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
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/reports";
            }
            if (!emailService.isConfigured()) {
                redirect.addFlashAttribute("warning", "SMTP chưa được cấu hình nên email nhắc quá hạn chưa được gửi.");
                return "redirect:/reports";
            }
            List<BorrowRecord> overdue = borrowDAO.findOverdue();
            if (overdue.isEmpty()) {
                redirect.addFlashAttribute("message", "Không có phiếu quá hạn nào cần nhắc.");
                return "redirect:/reports";
            }
            int sent = emailService.sendOverdueReminders(overdue);
            redirect.addFlashAttribute("message", "Đã gửi " + sent + " email nhắc quá hạn.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể gửi nhắc nhở: " + e.getMessage());
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

    private String statLinePoints(List<StatItem> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        int count = items.size();
        for (int i = 0; i < count; i++) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            double x = ((i + 0.5) * 100.0) / count;
            double y = 90.0 - (Math.min(100, items.get(i).getPercent()) * 0.75);
            builder.append(String.format(java.util.Locale.ROOT, "%.2f,%.2f", x, y));
        }
        return builder.toString();
    }
}

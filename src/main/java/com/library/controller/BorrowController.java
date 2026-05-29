package com.library.controller;

import com.library.dao.BookDAO;
import com.library.dao.BorrowDAO;
import com.library.dao.ReaderDAO;
import com.library.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;

@Controller
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowDAO borrowDAO;

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private ReaderDAO readerDAO;

    @GetMapping
    public String page(Model model, HttpSession session) throws Exception {
        if (!AuthUtil.canManageLibrary(session)) {
            return "redirect:/login";
        }
        model.addAttribute("books", bookDAO.findAll());
        model.addAttribute("readers", readerDAO.findAll());
        model.addAttribute("records", borrowDAO.findAll());
        return "borrow";
    }

    @PostMapping("/create")
    public String create(@RequestParam("bookId") int bookId,
                         @RequestParam("readerId") int readerId,
                         @RequestParam("borrowDate") Date borrowDate,
                         @RequestParam("dueDate") Date dueDate,
                         HttpSession session,
                         RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Permission denied.");
                return "redirect:/borrow";
            }
            borrowDAO.borrow(bookId, readerId, borrowDate, dueDate);
            redirect.addFlashAttribute("message", "Da tao phieu muon.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Khong the tao phieu muon: " + e.getMessage());
        }
        return "redirect:/borrow";
    }

    @GetMapping("/return/{id}")
    public String ret(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Permission denied.");
                return "redirect:/borrow";
            }
            borrowDAO.returnBook(id);
            redirect.addFlashAttribute("message", "Da tra sach.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Khong the tra sach: " + e.getMessage());
        }
        return "redirect:/borrow";
    }
}

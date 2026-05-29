package com.library.controller;

import com.library.dao.*;
import com.library.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    @Autowired
    BookDAO bookDAO;
    @Autowired
    ReaderDAO readerDAO;
    @Autowired
    BorrowDAO borrowDAO;

    @GetMapping("/dashboard")
    public String dashboard(Model m, HttpSession session) throws Exception {
        if (!AuthUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        m.addAttribute("books", bookDAO.findAll());
        m.addAttribute("readers", readerDAO.findAll());
        m.addAttribute("records", borrowDAO.findAll());
        m.addAttribute("statusStats", borrowDAO.countByStatus());
        m.addAttribute("topBooks", borrowDAO.topBorrowedBooks(5));
        return "dashboard";
    }
}

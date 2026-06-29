package com.library.controller;

import com.library.dao.UserDAO;
import com.library.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserDAO userDAO;

    @GetMapping({"/", "/login"})
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession session) {

        try {
            User user = userDAO.login(username, password);

            if (user != null) {
                session.setAttribute("user", user);
                return "redirect:/dashboard";
            }

            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu.");
            return "login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("fullName") String fullName,
                           Model model) {
        try {
            userDAO.register(username, password, fullName);
            model.addAttribute("message", "Đăng ký thành công. Vui lòng đăng nhập.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể đăng ký: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

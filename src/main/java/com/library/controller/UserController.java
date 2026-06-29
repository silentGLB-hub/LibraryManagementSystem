package com.library.controller;

import com.library.dao.UserDAO;
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

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDAO userDAO;

    @GetMapping
    public String page(Model model, HttpSession session) {
        if (!AuthUtil.canManageUsers(session)) {
            return "redirect:/login";
        }
        model.addAttribute("users", userDAO.findAll());
        return "users";
    }

    @PostMapping("/{id}/role")
    public String updateRole(@PathVariable("id") int id,
                             @RequestParam("role") String role,
                             HttpSession session,
                             RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageUsers(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/users";
            }
            userDAO.updateRole(id, role);
            redirect.addFlashAttribute("message", "Đã cập nhật vai trò.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể cập nhật vai trò: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}/contact")
    public String updateContact(@PathVariable("id") int id,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "phone", required = false) String phone,
                                HttpSession session,
                                RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageUsers(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/users";
            }
            userDAO.updateContact(id, email, phone);
            redirect.addFlashAttribute("message", "Đã cập nhật thông tin liên hệ.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể cập nhật liên hệ: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageUsers(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/users";
            }
            if (AuthUtil.currentUser(session) != null && AuthUtil.currentUser(session).getId() == id) {
                redirect.addFlashAttribute("error", "Bạn không thể xóa tài khoản của chính mình.");
                return "redirect:/users";
            }
            userDAO.delete(id);
            redirect.addFlashAttribute("message", "Đã xóa người dùng.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể xóa người dùng: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
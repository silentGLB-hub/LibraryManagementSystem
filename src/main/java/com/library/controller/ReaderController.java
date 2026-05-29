package com.library.controller;

import com.library.dao.ReaderDAO;
import com.library.model.Reader;
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
@RequestMapping("/readers")
public class ReaderController {

    @Autowired
    private ReaderDAO readerDAO;

    @GetMapping
    public String list(Model model, HttpSession session) {
        if (!AuthUtil.canManageLibrary(session)) {
            return "redirect:/login";
        }
        model.addAttribute("readers", readerDAO.findAll());
        return "readers";
    }

    @PostMapping("/save")
    public String save(@RequestParam("fullName") String fullName,
                       @RequestParam(value = "email", required = false) String email,
                       @RequestParam(value = "phone", required = false) String phone,
                       @RequestParam(value = "address", required = false) String address,
                       HttpSession session,
                       RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Permission denied.");
                return "redirect:/readers";
            }
            Reader reader = new Reader();
            reader.setFullName(fullName);
            reader.setEmail(email);
            reader.setPhone(phone);
            reader.setAddress(address);
            readerDAO.save(reader);
            redirect.addFlashAttribute("message", "Reader saved.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/readers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Permission denied.");
                return "redirect:/readers";
            }
            readerDAO.delete(id);
            redirect.addFlashAttribute("message", "Reader deleted.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/readers";
    }
}

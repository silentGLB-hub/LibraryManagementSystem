package com.library.controller;

import com.library.dao.CatalogDAO;
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
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private CatalogDAO catalogDAO;

    @GetMapping
    public String page(Model model, HttpSession session) throws Exception {
        if (!AuthUtil.canManageLibrary(session)) {
            return "redirect:/login";
        }
        model.addAttribute("categories", catalogDAO.categories());
        model.addAttribute("authors", catalogDAO.authors());
        model.addAttribute("publishers", catalogDAO.publishers());
        return "catalog";
    }

    @PostMapping("/{type}/save")
    public String save(@PathVariable("type") String type,
                       @RequestParam("name") String name,
                       HttpSession session,
                       RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Permission denied.");
                return "redirect:/catalog";
            }
            if ("category".equals(type)) {
                catalogDAO.addCategory(name);
            } else if ("author".equals(type)) {
                catalogDAO.addAuthor(name);
            } else if ("publisher".equals(type)) {
                catalogDAO.addPublisher(name);
            } else {
                throw new IllegalArgumentException("Invalid catalog type.");
            }
            redirect.addFlashAttribute("message", "Saved.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/catalog";
    }

    @GetMapping("/{type}/delete/{id}")
    public String delete(@PathVariable("type") String type,
                         @PathVariable("id") int id,
                         HttpSession session,
                         RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Permission denied.");
                return "redirect:/catalog";
            }
            if ("category".equals(type)) {
                catalogDAO.deleteCategory(id);
            } else if ("author".equals(type)) {
                catalogDAO.deleteAuthor(id);
            } else if ("publisher".equals(type)) {
                catalogDAO.deletePublisher(id);
            } else {
                throw new IllegalArgumentException("Invalid catalog type.");
            }
            redirect.addFlashAttribute("message", "Deleted.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Cannot delete item that is being used.");
        }
        return "redirect:/catalog";
    }
}

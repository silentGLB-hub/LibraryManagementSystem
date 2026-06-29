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
                       @RequestParam(value = "infoUrl", required = false) String infoUrl,
                       HttpSession session,
                       RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/catalog";
            }
            if ("category".equals(type)) {
                catalogDAO.addCategory(name, infoUrl);
            } else if ("author".equals(type)) {
                catalogDAO.addAuthor(name, infoUrl);
            } else if ("publisher".equals(type)) {
                catalogDAO.addPublisher(name, infoUrl);
            } else {
                throw new IllegalArgumentException("Loại danh mục không hợp lệ.");
            }
            redirect.addFlashAttribute("message", "Đã lưu.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/catalog";
    }

    @PostMapping("/{type}/update/{id}")
    public String update(@PathVariable("type") String type,
                         @PathVariable("id") int id,
                         @RequestParam("name") String name,
                         @RequestParam(value = "infoUrl", required = false) String infoUrl,
                         HttpSession session,
                         RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/catalog";
            }
            if ("category".equals(type)) {
                catalogDAO.updateCategory(id, name, infoUrl);
            } else if ("author".equals(type)) {
                catalogDAO.updateAuthor(id, name, infoUrl);
            } else if ("publisher".equals(type)) {
                catalogDAO.updatePublisher(id, name, infoUrl);
            } else {
                throw new IllegalArgumentException("Loại danh mục không hợp lệ.");
            }
            redirect.addFlashAttribute("message", "Đã cập nhật.");
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
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/catalog";
            }
            if ("category".equals(type)) {
                catalogDAO.deleteCategory(id);
            } else if ("author".equals(type)) {
                catalogDAO.deleteAuthor(id);
            } else if ("publisher".equals(type)) {
                catalogDAO.deletePublisher(id);
            } else {
                throw new IllegalArgumentException("Loại danh mục không hợp lệ.");
            }
            redirect.addFlashAttribute("message", "Đã xóa.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể xóa mục đang được sử dụng.");
        }
        return "redirect:/catalog";
    }
}
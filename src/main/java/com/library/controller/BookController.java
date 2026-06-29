package com.library.controller;

import com.library.dao.BookDAO;
import com.library.dao.CatalogDAO;
import com.library.model.Book;
import com.library.util.AuthUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private CatalogDAO catalogDAO;

    @Autowired
    private ServletContext servletContext;

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model, HttpSession session) throws Exception {
        if (!AuthUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }
        model.addAttribute("books", bookDAO.search(q));
        model.addAttribute("categories", catalogDAO.categories());
        model.addAttribute("authors", catalogDAO.authors());
        model.addAttribute("publishers", catalogDAO.publishers());
        model.addAttribute("canManage", AuthUtil.canManageLibrary(session));
        model.addAttribute("q", q);
        return "books";
    }

    @GetMapping("/save")
    public String saveFormFallback() {
        return "redirect:/books";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "code", required = false) String code,
                       @RequestParam(value = "title", required = false) String title,
                       @RequestParam(value = "quantity", required = false) Integer quantity,
                       @RequestParam(value = "categoryId", required = false, defaultValue = "0") int categoryId,
                       @RequestParam(value = "authorId", required = false, defaultValue = "0") int authorId,
                       @RequestParam(value = "publisherId", required = false, defaultValue = "0") int publisherId,
                       @RequestParam(value = "coverImage", required = false) String coverImage,
                       @RequestParam(value = "previewText", required = false) String previewText,
                       @RequestParam(value = "chapters", required = false) String chapters,
                       @RequestParam(value = "contentText", required = false) String contentText,
                       @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                       @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,
                       HttpSession session,
                       RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/books";
            }
            if (isBlank(code) || isBlank(title) || quantity == null) {
                redirect.addFlashAttribute("error", "Mã sách, tên sách và số lượng là bắt buộc.");
                return "redirect:/books";
            }
            if (quantity < 0) {
                redirect.addFlashAttribute("error", "Số lượng không được âm.");
                return "redirect:/books";
            }

            String uploadedCover = storeCoverImage(coverFile);
            String uploadedPdf = storePdfFile(pdfFile);
            Book book = new Book();
            book.setCode(code);
            book.setTitle(title);
            book.setQuantity(quantity);
            book.setCoverImage(uploadedCover != null ? uploadedCover : coverImage);
            book.setPreviewText(previewText);
            book.setChapters(chapters);
            book.setContentText(contentText);
            book.setPdfFile(uploadedPdf);
            bookDAO.save(book, categoryId, authorId, publisherId);
            redirect.addFlashAttribute("message", "Đã thêm sách.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể lưu sách: " + e.getMessage());
        }
        return "redirect:/books";
    }

    @PostMapping("/{id}/cover")
    public String updateCover(@PathVariable("id") int id,
                              @RequestParam(value = "coverImage", required = false) String coverImage,
                              @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                              HttpSession session,
                              RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/books";
            }
            String uploadedCover = storeCoverImage(coverFile);
            if (uploadedCover == null && isBlank(coverImage)) {
                redirect.addFlashAttribute("error", "Hãy chọn file bìa hoặc nhập URL/đường dẫn ảnh bìa.");
                return "redirect:/books";
            }
            bookDAO.updateCoverImage(id, uploadedCover != null ? uploadedCover : coverImage);
            redirect.addFlashAttribute("message", "Đã cập nhật bìa sách.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể cập nhật bìa sách: " + e.getMessage());
        }
        return "redirect:/books";
    }

    @PostMapping("/{id}/pdf")
    public String updatePdf(@PathVariable("id") int id,
                            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,
                            HttpSession session,
                            RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/books";
            }
            String uploadedPdf = storePdfFile(pdfFile);
            if (uploadedPdf == null) {
                redirect.addFlashAttribute("error", "Hãy chọn file PDF để tải lên.");
                return "redirect:/books";
            }
            bookDAO.updatePdfFile(id, uploadedPdf);
            redirect.addFlashAttribute("message", "Đã tải PDF sách lên.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể tải PDF lên: " + e.getMessage());
        }
        return "redirect:/books";
    }

    @PostMapping("/{id}/reading")
    public String updateReading(@PathVariable("id") int id,
                                @RequestParam(value = "previewText", required = false) String previewText,
                                @RequestParam(value = "chapters", required = false) String chapters,
                                @RequestParam(value = "contentText", required = false) String contentText,
                                HttpSession session,
                                RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/books";
            }
            bookDAO.updateReadingContent(id, previewText, contentText, chapters);
            redirect.addFlashAttribute("message", "Đã cập nhật nội dung đọc của sách.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể cập nhật nội dung đọc: " + e.getMessage());
        }
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirect) {
        try {
            if (!AuthUtil.canManageLibrary(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/books";
            }
            bookDAO.delete(id);
            redirect.addFlashAttribute("message", "Đã xóa sách.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books";
    }

    private String storeCoverImage(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT)
                : "";
        if (!extension.matches("png|jpg|jpeg|gif|webp|svg")) {
            throw new IllegalArgumentException("Ảnh bìa phải là png, jpg, jpeg, gif, webp hoặc svg.");
        }

        String uploadRoot = servletContext.getRealPath("/assets/uploads");
        if (uploadRoot == null) {
            uploadRoot = new File(System.getProperty("java.io.tmpdir"), "library-uploads").getAbsolutePath();
        }
        Files.createDirectories(Path.of(uploadRoot));
        String fileName = UUID.randomUUID() + "." + extension;
        Path destination = Path.of(uploadRoot, fileName);
        file.transferTo(destination);
        return "/assets/uploads/" + fileName;
    }

    private String storePdfFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT)
                : "";
        if (!"pdf".equals(extension)) {
            throw new IllegalArgumentException("File sách phải là PDF.");
        }

        String uploadRoot = servletContext.getRealPath("/assets/books");
        if (uploadRoot == null) {
            uploadRoot = new File(System.getProperty("java.io.tmpdir"), "library-book-pdfs").getAbsolutePath();
        }
        Files.createDirectories(Path.of(uploadRoot));
        String fileName = UUID.randomUUID() + ".pdf";
        Path destination = Path.of(uploadRoot, fileName);
        file.transferTo(destination);
        return "/assets/books/" + fileName;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
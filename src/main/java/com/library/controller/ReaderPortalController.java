package com.library.controller;

import com.library.dao.BookDAO;
import com.library.dao.BorrowDAO;
import com.library.dao.ReaderDAO;
import com.library.dao.UserDAO;
import com.library.model.Book;
import com.library.model.Reader;
import com.library.model.User;
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
import java.time.LocalDate;

@Controller
@RequestMapping("/reader")
public class ReaderPortalController {

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private BorrowDAO borrowDAO;

    @Autowired
    private ReaderDAO readerDAO;

    @Autowired
    private UserDAO userDAO;

    @GetMapping("/history")
    public String history(Model model, HttpSession session) throws Exception {
        if (!AuthUtil.isReader(session)) {
            return "redirect:/login";
        }
        User user = AuthUtil.currentUser(session);
        Reader reader = readerDAO.findOrCreateForAccount(user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone());
        model.addAttribute("readerProfile", reader);
        model.addAttribute("books", bookDAO.findAll());
        model.addAttribute("records", reader == null ? java.util.Collections.emptyList() : borrowDAO.findByReaderId(reader.getId()));
        return "reader-history";
    }

    @PostMapping("/contact")
    public String updateContact(@RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "phone", required = false) String phone,
                                HttpSession session,
                                RedirectAttributes redirect) {
        try {
            if (!AuthUtil.isReader(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/login";
            }
            User user = AuthUtil.currentUser(session);
            Reader reader = readerDAO.findOrCreateForAccount(user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone());
            if (reader == null) {
                redirect.addFlashAttribute("error", "Tài khoản của bạn chưa liên kết với hồ sơ độc giả.");
                return "redirect:/reader/history";
            }
            readerDAO.updateContact(reader.getId(), email, phone);
            userDAO.updateContactForUsername(user.getUsername(), email, phone);
            user.setEmail(email == null || email.trim().isEmpty() ? null : email.trim());
            user.setPhone(phone == null || phone.trim().isEmpty() ? null : phone.trim());
            session.setAttribute("user", user);
            redirect.addFlashAttribute("message", "Thông tin liên hệ của bạn đã được cập nhật.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể cập nhật liên hệ: " + e.getMessage());
        }
        return "redirect:/reader/history";
    }

    @PostMapping("/request")
    public String requestBorrow(@RequestParam("bookId") int bookId,
                                @RequestParam("borrowDate") String borrowDate,
                                @RequestParam("dueDate") String dueDate,
                                HttpSession session,
                                RedirectAttributes redirect) {
        try {
            if (!AuthUtil.isReader(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/books";
            }
            User user = AuthUtil.currentUser(session);
            Reader reader = readerDAO.findOrCreateForAccount(user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone());
            if (reader == null) {
                redirect.addFlashAttribute("error", "Tài khoản của bạn chưa liên kết với hồ sơ độc giả. Hãy nhờ thủ thư tạo hồ sơ bằng họ tên hoặc email của bạn.");
                return "redirect:/books";
            }
            LocalDate borrow = LocalDate.parse(borrowDate);
            LocalDate due = LocalDate.parse(dueDate);
            if (due.isBefore(borrow)) {
                redirect.addFlashAttribute("error", "Ngày trả phải sau hoặc bằng ngày mượn.");
                return "redirect:/reader/history";
            }
            borrowDAO.borrow(bookId, reader.getId(), Date.valueOf(borrow), Date.valueOf(due));
            redirect.addFlashAttribute("message", "Đã gửi yêu cầu mượn. Bạn có thể đọc sách trong lịch sử mượn khi phiếu còn hoạt động.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể gửi yêu cầu mượn: " + e.getMessage());
        }
        return "redirect:/reader/history";
    }


    @PostMapping("/return/{recordId}")
    public String returnBorrowedBook(@PathVariable("recordId") int recordId,
                                     HttpSession session,
                                     RedirectAttributes redirect) {
        try {
            if (!AuthUtil.isReader(session)) {
                redirect.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
                return "redirect:/login";
            }
            User user = AuthUtil.currentUser(session);
            Reader reader = readerDAO.findOrCreateForAccount(user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone());
            if (reader == null) {
                redirect.addFlashAttribute("error", "Tài khoản của bạn chưa liên kết với hồ sơ độc giả.");
                return "redirect:/reader/history";
            }
            borrowDAO.returnBookForReader(recordId, reader.getId());
            redirect.addFlashAttribute("message", "Đã trả sách thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể trả sách: " + e.getMessage());
        }
        return "redirect:/reader/history";
    }
    @GetMapping("/books/{bookId}/preview")
    public String preview(@PathVariable("bookId") int bookId, Model model, HttpSession session) throws Exception {
        if (!AuthUtil.isReader(session)) {
            return "redirect:/login";
        }
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        return "book-preview";
    }

    @GetMapping("/books/{bookId}/read")
    public String read(@PathVariable("bookId") int bookId, Model model, HttpSession session, RedirectAttributes redirect) throws Exception {
        if (!AuthUtil.isReader(session)) {
            return "redirect:/login";
        }
        User user = AuthUtil.currentUser(session);
        Reader reader = readerDAO.findOrCreateForAccount(user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone());
        if (reader == null || !borrowDAO.hasActiveBorrow(reader.getId(), bookId)) {
            redirect.addFlashAttribute("error", "Bạn chỉ có thể đọc sách này sau khi có phiếu mượn đang hoạt động.");
            return "redirect:/reader/history";
        }
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            redirect.addFlashAttribute("error", "Sách không tồn tại.");
            return "redirect:/reader/history";
        }
        model.addAttribute("book", book);
        model.addAttribute("reader", reader);
        return "book-reader";
    }
}
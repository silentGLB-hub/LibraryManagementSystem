package com.library.dao;

import com.library.config.DbUtil;
import com.library.model.BorrowRecord;
import com.library.model.ChartPoint;
import com.library.model.StatItem;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BorrowDAO {

    public void borrow(int bookId, int readerId, Date borrowDate, Date dueDate) throws Exception {
        if (borrowDate == null || dueDate == null || dueDate.before(borrowDate)) {
            throw new IllegalArgumentException("Hạn trả phải sau hoặc bằng ngày mượn.");
        }

        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement up = c.prepareStatement("UPDATE books SET available = available - 1 WHERE id = ? AND available > 0");
                 PreparedStatement ps = c.prepareStatement("INSERT INTO borrow_records(book_id,reader_id,borrow_date,due_date,status,fine) VALUES(?,?,?,?,'BORROWING',0)")) {

                up.setInt(1, bookId);
                int rows = up.executeUpdate();
                if (rows == 0) {
                    throw new IllegalStateException("Sách hiện không còn khả dụng.");
                }

                ps.setInt(1, bookId);
                ps.setInt(2, readerId);
                ps.setDate(3, borrowDate);
                ps.setDate(4, dueDate);
                ps.executeUpdate();
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        }
    }

    public void returnBook(int id) throws Exception {
        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement find = c.prepareStatement(
                    "SELECT book_id, DATEDIFF(CURDATE(), due_date) late FROM borrow_records WHERE id = ? AND status <> 'RETURNED'");
                 PreparedStatement up = c.prepareStatement(
                         "UPDATE borrow_records SET return_date = CURDATE(), status = 'RETURNED', fine = ? WHERE id = ? AND status <> 'RETURNED'");
                 PreparedStatement book = c.prepareStatement("UPDATE books SET available = LEAST(quantity, available + 1) WHERE id = ?")) {

                find.setInt(1, id);
                try (ResultSet rs = find.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalStateException("Phiếu mượn không tồn tại hoặc đã được trả.");
                    }

                    int late = Math.max(0, rs.getInt("late"));
                    BigDecimal fine = BigDecimal.valueOf(late * 5000L);
                    up.setBigDecimal(1, fine);
                    up.setInt(2, id);
                    up.executeUpdate();

                    book.setInt(1, rs.getInt("book_id"));
                    book.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        }
    }


    public void returnBookForReader(int id, int readerId) throws Exception {
        try (Connection c = DbUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement find = c.prepareStatement(
                    "SELECT book_id FROM borrow_records "
                            + "WHERE id = ? AND reader_id = ? AND status = 'BORROWING' "
                            + "AND return_date IS NULL AND due_date >= CURDATE()");
                 PreparedStatement up = c.prepareStatement(
                         "UPDATE borrow_records SET return_date = CURDATE(), status = 'RETURNED', fine = 0 WHERE id = ? AND reader_id = ?");
                 PreparedStatement book = c.prepareStatement("UPDATE books SET available = LEAST(quantity, available + 1) WHERE id = ?")) {

                find.setInt(1, id);
                find.setInt(2, readerId);
                try (ResultSet rs = find.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalStateException("Độc giả chỉ được tự trả sách khi chưa quá hạn. Sách quá hạn phải do thủ thư xử lý.");
                    }
                    int bookId = rs.getInt("book_id");
                    up.setInt(1, id);
                    up.setInt(2, readerId);
                    up.executeUpdate();
                    book.setInt(1, bookId);
                    book.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        }
    }
    public List<BorrowRecord> findAll() {
        List<BorrowRecord> records = new ArrayList<>();
        refreshOverdueStatus();

        String sql = "SELECT br.*, b.title bookTitle, r.full_name readerName "
                + ", r.email readerEmail "
                + "FROM borrow_records br "
                + "JOIN books b ON br.book_id = b.id "
                + "JOIN readers r ON br.reader_id = r.id "
                + "ORDER BY br.id DESC";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BorrowRecord br = new BorrowRecord();
                br.setId(rs.getInt("id"));
                br.setBookId(rs.getInt("book_id"));
                br.setReaderId(rs.getInt("reader_id"));
                br.setBookTitle(rs.getString("bookTitle"));
                br.setReaderName(rs.getString("readerName"));
                br.setReaderEmail(rs.getString("readerEmail"));
                br.setBorrowDate(rs.getDate("borrow_date"));
                br.setDueDate(rs.getDate("due_date"));
                br.setReturnDate(rs.getDate("return_date"));
                br.setStatus(rs.getString("status"));
                br.setFine(rs.getBigDecimal("fine"));
                br.setReaderReturnAllowed(canReaderReturn(br));
                records.add(br);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }


    public BorrowRecord findById(int id) {
        refreshOverdueStatus();
        String sql = "SELECT br.*, b.title bookTitle, r.full_name readerName, r.email readerEmail "
                + "FROM borrow_records br "
                + "JOIN books b ON br.book_id = b.id "
                + "JOIN readers r ON br.reader_id = r.id "
                + "WHERE br.id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BorrowRecord br = new BorrowRecord();
                    br.setId(rs.getInt("id"));
                    br.setBookId(rs.getInt("book_id"));
                    br.setReaderId(rs.getInt("reader_id"));
                    br.setBookTitle(rs.getString("bookTitle"));
                    br.setReaderName(rs.getString("readerName"));
                    br.setReaderEmail(rs.getString("readerEmail"));
                    br.setBorrowDate(rs.getDate("borrow_date"));
                    br.setDueDate(rs.getDate("due_date"));
                    br.setReturnDate(rs.getDate("return_date"));
                    br.setStatus(rs.getString("status"));
                    br.setFine(rs.getBigDecimal("fine"));
                br.setReaderReturnAllowed(canReaderReturn(br));
                    return br;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<BorrowRecord> findOverdue() {
        refreshOverdueStatus();
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.*, b.title bookTitle, r.full_name readerName, r.email readerEmail "
                + "FROM borrow_records br "
                + "JOIN books b ON br.book_id = b.id "
                + "JOIN readers r ON br.reader_id = r.id "
                + "WHERE br.status = 'OVERDUE' AND br.return_date IS NULL "
                + "ORDER BY br.due_date ASC";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BorrowRecord br = new BorrowRecord();
                br.setId(rs.getInt("id"));
                br.setBookId(rs.getInt("book_id"));
                br.setReaderId(rs.getInt("reader_id"));
                br.setBookTitle(rs.getString("bookTitle"));
                br.setReaderName(rs.getString("readerName"));
                br.setReaderEmail(rs.getString("readerEmail"));
                br.setBorrowDate(rs.getDate("borrow_date"));
                br.setDueDate(rs.getDate("due_date"));
                br.setReturnDate(rs.getDate("return_date"));
                br.setStatus(rs.getString("status"));
                br.setFine(rs.getBigDecimal("fine"));
                br.setReaderReturnAllowed(canReaderReturn(br));
                records.add(br);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }


    public List<BorrowRecord> findByReaderId(int readerId) {
        refreshOverdueStatus();
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT br.*, b.title bookTitle, r.full_name readerName, r.email readerEmail "
                + "FROM borrow_records br "
                + "JOIN books b ON br.book_id = b.id "
                + "JOIN readers r ON br.reader_id = r.id "
                + "WHERE br.reader_id = ? "
                + "ORDER BY br.id DESC";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, readerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord br = new BorrowRecord();
                    br.setId(rs.getInt("id"));
                    br.setBookId(rs.getInt("book_id"));
                    br.setReaderId(rs.getInt("reader_id"));
                    br.setBookTitle(rs.getString("bookTitle"));
                    br.setReaderName(rs.getString("readerName"));
                    br.setReaderEmail(rs.getString("readerEmail"));
                    br.setBorrowDate(rs.getDate("borrow_date"));
                    br.setDueDate(rs.getDate("due_date"));
                    br.setReturnDate(rs.getDate("return_date"));
                    br.setStatus(rs.getString("status"));
                    br.setFine(rs.getBigDecimal("fine"));
                br.setReaderReturnAllowed(canReaderReturn(br));
                    records.add(br);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }
    public List<StatItem> countByStatus() {
        refreshOverdueStatus();
        List<StatItem> items = new ArrayList<>();
        int total = 0;
        String sql = "SELECT status, COUNT(*) total FROM borrow_records GROUP BY status ORDER BY status";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int value = rs.getInt("total");
                items.add(new StatItem(rs.getString("status"), value, 0));
                total += value;
            }
            for (StatItem item : items) {
                item.setPercent(total == 0 ? 0 : Math.max(4, (item.getValue() * 100) / total));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<StatItem> topBorrowedBooks(int limit) {
        List<StatItem> items = new ArrayList<>();
        int max = 0;
        String sql = "SELECT b.title, COUNT(*) total "
                + "FROM borrow_records br JOIN books b ON br.book_id = b.id "
                + "GROUP BY b.id, b.title ORDER BY total DESC LIMIT ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int value = rs.getInt("total");
                    items.add(new StatItem(rs.getString("title"), value, 0));
                    max = Math.max(max, value);
                }
            }
            for (StatItem item : items) {
                item.setPercent(max == 0 ? 0 : Math.max(6, (item.getValue() * 100) / max));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<StatItem> borrowCountByMonth() {
        List<StatItem> items = new ArrayList<>();
        int max = 0;
        String sql = "SELECT DATE_FORMAT(borrow_date, '%m/%Y') label, COUNT(*) total, "
                + "YEAR(borrow_date) y, MONTH(borrow_date) m "
                + "FROM borrow_records "
                + "GROUP BY YEAR(borrow_date), MONTH(borrow_date), DATE_FORMAT(borrow_date, '%m/%Y') "
                + "ORDER BY y DESC, m DESC LIMIT 12";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int value = rs.getInt("total");
                items.add(new StatItem(rs.getString("label"), value, 0));
                max = Math.max(max, value);
            }
            applyPercentByMax(items, max, 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<StatItem> borrowCountByYear() {
        List<StatItem> items = new ArrayList<>();
        int max = 0;
        String sql = "SELECT YEAR(borrow_date) label, COUNT(*) total "
                + "FROM borrow_records "
                + "GROUP BY YEAR(borrow_date) "
                + "ORDER BY label DESC LIMIT 6";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int value = rs.getInt("total");
                items.add(new StatItem(rs.getString("label"), value, 0));
                max = Math.max(max, value);
            }
            applyPercentByMax(items, max, 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<StatItem> readerActivity(int limit) {
        List<StatItem> items = new ArrayList<>();
        int max = 0;
        String sql = "SELECT r.full_name, COUNT(br.id) total "
                + "FROM readers r "
                + "LEFT JOIN borrow_records br ON br.reader_id = r.id "
                + "GROUP BY r.id, r.full_name "
                + "ORDER BY total DESC, r.full_name ASC LIMIT ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int value = rs.getInt("total");
                    items.add(new StatItem(rs.getString("full_name"), value, 0));
                    max = Math.max(max, value);
                }
            }
            applyPercentByMax(items, max, 6);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<ChartPoint> monthlyBorrowReturnChart() {
        List<ChartPoint> points = new ArrayList<>();
        int maxBar = 0;
        int maxLine = 0;
        String sql = "SELECT * FROM ("
                + "SELECT DATE_FORMAT(borrow_date, '%m/%Y') label, "
                + "COUNT(*) borrowed, "
                + "SUM(CASE WHEN return_date IS NOT NULL THEN 1 ELSE 0 END) returned, "
                + "YEAR(borrow_date) y, MONTH(borrow_date) m "
                + "FROM borrow_records "
                + "GROUP BY YEAR(borrow_date), MONTH(borrow_date), DATE_FORMAT(borrow_date, '%m/%Y') "
                + "ORDER BY y DESC, m DESC LIMIT 12"
                + ") recent_months ORDER BY y ASC, m ASC";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ChartPoint point = new ChartPoint();
                point.setLabel(rs.getString("label"));
                point.setBarValue(rs.getInt("borrowed"));
                point.setLineValue(rs.getInt("returned"));
                points.add(point);
                maxBar = Math.max(maxBar, point.getBarValue());
                maxLine = Math.max(maxLine, point.getLineValue());
            }
            applyChartGeometry(points, maxBar, maxLine);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return points;
    }


    public boolean hasActiveBorrow(int readerId, int bookId) {
        refreshOverdueStatus();
        String sql = "SELECT COUNT(*) FROM borrow_records "
                + "WHERE reader_id = ? AND book_id = ? AND return_date IS NULL AND status IN ('BORROWING','OVERDUE')";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, readerId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean canReaderReturn(BorrowRecord br) {
        return "BORROWING".equals(br.getStatus())
                && br.getReturnDate() == null
                && br.getDueDate() != null
                && !br.getDueDate().toLocalDate().isBefore(java.time.LocalDate.now());
    }
    private void refreshOverdueStatus() {
        String sql = "UPDATE borrow_records SET status = 'OVERDUE' "
                + "WHERE status = 'BORROWING' AND return_date IS NULL AND due_date < CURDATE()";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyPercentByMax(List<StatItem> items, int max, int minimumPercent) {
        for (StatItem item : items) {
            item.setPercent(max == 0 ? 0 : Math.max(minimumPercent, (item.getValue() * 100) / max));
        }
    }

    private void applyChartGeometry(List<ChartPoint> points, int maxBar, int maxLine) {
        int count = points.size();
        int maxBarValue = Math.max(1, maxBar);
        int maxLineValue = Math.max(1, maxLine);
        int baseY = 78;
        for (int i = 0; i < count; i++) {
            ChartPoint point = points.get(i);
            int barHeight = Math.max(6, (int) Math.round(point.getBarValue() * 54.0 / maxBarValue));
            point.setBarPercent(maxBarValue == 0 ? 0 : Math.max(6, (point.getBarValue() * 100) / maxBarValue));
            point.setBarHeight(barHeight);
            point.setBarY(baseY - barHeight);
            point.setX(count == 1 ? 50 : 12 + (int) Math.round(i * (76.0 / (count - 1))));
            point.setY(maxLine == 0 ? 54 : 58 - (int) Math.round(point.getLineValue() * 30.0 / maxLineValue));
        }
    }
}



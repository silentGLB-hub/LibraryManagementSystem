package com.library.util;

import com.library.model.BorrowRecord;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ReportExportUtil {

    private ReportExportUtil() {
    }

    public static void writeExcelHtml(List<BorrowRecord> records, OutputStream out) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset=\"UTF-8\"></head><body>");
        html.append("<table border=\"1\"><tr><th>Sách</th><th>Độc giả</th><th>Ngày mượn</th><th>Hạn trả</th><th>Ngày trả</th><th>Trạng thái</th><th>Tiền phạt</th></tr>");
        for (BorrowRecord r : records) {
            html.append("<tr>")
                    .append(td(r.getBookTitle()))
                    .append(td(r.getReaderName()))
                    .append(td(String.valueOf(r.getBorrowDate())))
                    .append(td(String.valueOf(r.getDueDate())))
                    .append(td(r.getReturnDate() == null ? "" : String.valueOf(r.getReturnDate())))
                    .append(td(r.getStatus()))
                    .append(td(String.valueOf(r.getFine())))
                    .append("</tr>");
        }
        html.append("</table></body></html>");
        out.write(html.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static void writePdf(List<BorrowRecord> records, OutputStream out) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("Báo cáo mượn sách thư viện");
        lines.add("Sách | Độc giả | Hạn trả | Trạng thái | Tiền phạt");
        for (BorrowRecord r : records) {
            lines.add(safe(r.getBookTitle()) + " | " + safe(r.getReaderName()) + " | "
                    + r.getDueDate() + " | " + safe(r.getStatus()) + " | " + r.getFine());
        }

        StringBuilder content = new StringBuilder();
        content.append("BT /F1 12 Tf 40 790 Td 14 TL ");
        for (String line : lines) {
            content.append("(").append(escapePdf(line)).append(") Tj T* ");
        }
        content.append("ET");
        byte[] stream = content.toString().getBytes(StandardCharsets.ISO_8859_1);

        List<byte[]> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>".getBytes(StandardCharsets.ISO_8859_1));
        objects.add("<< /Type /Pages /Kids [3 0 R] /Count 1 >>".getBytes(StandardCharsets.ISO_8859_1));
        objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>".getBytes(StandardCharsets.ISO_8859_1));
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>".getBytes(StandardCharsets.ISO_8859_1));
        objects.add(("<< /Length " + stream.length + " >>\nstream\n" + new String(stream, StandardCharsets.ISO_8859_1) + "\nendstream").getBytes(StandardCharsets.ISO_8859_1));

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.length());
            pdf.append(i + 1).append(" 0 obj\n")
                    .append(new String(objects.get(i), StandardCharsets.ISO_8859_1))
                    .append("\nendobj\n");
        }
        int xref = pdf.length();
        pdf.append("xref\n0 ").append(objects.size() + 1).append("\n0000000000 65535 f \n");
        for (Integer offset : offsets) {
            pdf.append(String.format("%010d 00000 n \n", offset));
        }
        pdf.append("trailer << /Root 1 0 R /Size ").append(objects.size() + 1).append(" >>\n")
                .append("startxref\n").append(xref).append("\n%%EOF");
        out.write(pdf.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    private static String td(String value) {
        return "<td>" + escapeHtml(value) + "</td>";
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String escapePdf(String value) {
        return safe(value).replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^\\x20-\\x7E]", "?");
    }
}

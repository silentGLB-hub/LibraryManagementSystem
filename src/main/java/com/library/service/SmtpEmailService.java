package com.library.service;

import com.library.model.BorrowRecord;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class SmtpEmailService {

    public boolean isConfigured() {
        return !MailSettings.value("library.smtp.host", "LIBRARY_SMTP_HOST", "").isEmpty()
                && !MailSettings.value("library.smtp.from", "LIBRARY_SMTP_FROM",
                        MailSettings.value("library.smtp.user", "LIBRARY_SMTP_USER", "")).isEmpty();
    }

    public int sendOverdueReminders(List<BorrowRecord> overdueRecords) throws Exception {
        MailSettings settings = MailSettings.load();
        int sent = 0;
        for (BorrowRecord record : overdueRecords) {
            if (record.getReaderEmail() == null || record.getReaderEmail().trim().isEmpty()) {
                continue;
            }
            send(settings, record.getReaderEmail(), "Library overdue reminder",
                    "Hello " + record.getReaderName() + ",\n\n"
                            + "The book \"" + record.getBookTitle() + "\" was due on " + record.getDueDate() + ".\n"
                            + "Please return it as soon as possible.\n\nLibrary Management System");
            sent++;
        }
        return sent;
    }

    private void send(MailSettings settings, String to, String subject, String body) throws Exception {
        Socket socket = settings.ssl
                ? SSLSocketFactory.getDefault().createSocket(settings.host, settings.port)
                : new Socket(settings.host, settings.port);

        try (Socket s = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {

            expect(in, 220);
            command(in, out, "EHLO localhost", 250);

            if (settings.tls && !settings.ssl) {
                command(in, out, "STARTTLS", 220);
                Socket tls = ((SSLSocketFactory) SSLSocketFactory.getDefault())
                        .createSocket(s, settings.host, settings.port, true);
                try (BufferedReader tlsIn = new BufferedReader(new InputStreamReader(tls.getInputStream(), StandardCharsets.UTF_8));
                     BufferedWriter tlsOut = new BufferedWriter(new OutputStreamWriter(tls.getOutputStream(), StandardCharsets.UTF_8))) {
                    command(tlsIn, tlsOut, "EHLO localhost", 250);
                    authenticateIfNeeded(tlsIn, tlsOut, settings);
                    sendMessage(tlsIn, tlsOut, settings.from, to, subject, body);
                }
                return;
            }

            authenticateIfNeeded(in, out, settings);
            sendMessage(in, out, settings.from, to, subject, body);
        }
    }

    private void authenticateIfNeeded(BufferedReader in, BufferedWriter out, MailSettings settings) throws Exception {
        if (settings.username == null || settings.username.isEmpty()) {
            return;
        }
        command(in, out, "AUTH LOGIN", 334);
        command(in, out, Base64.getEncoder().encodeToString(settings.username.getBytes(StandardCharsets.UTF_8)), 334);
        command(in, out, Base64.getEncoder().encodeToString(settings.password.getBytes(StandardCharsets.UTF_8)), 235);
    }

    private void sendMessage(BufferedReader in, BufferedWriter out, String from, String to, String subject, String body) throws Exception {
        command(in, out, "MAIL FROM:<" + from + ">", 250);
        command(in, out, "RCPT TO:<" + to + ">", 250);
        command(in, out, "DATA", 354);
        out.write("From: " + from + "\r\n");
        out.write("To: " + to + "\r\n");
        out.write("Subject: " + subject + "\r\n");
        out.write("Content-Type: text/plain; charset=UTF-8\r\n");
        out.write("\r\n");
        out.write(body.replace("\n", "\r\n"));
        out.write("\r\n.\r\n");
        out.flush();
        expect(in, 250);
        command(in, out, "QUIT", 221);
    }

    private void command(BufferedReader in, BufferedWriter out, String command, int expectedCode) throws Exception {
        out.write(command + "\r\n");
        out.flush();
        expect(in, expectedCode);
    }

    private void expect(BufferedReader in, int expectedCode) throws Exception {
        String line = in.readLine();
        if (line == null || !line.startsWith(String.valueOf(expectedCode))) {
            throw new IllegalStateException("SMTP error: " + line);
        }
        while (line.length() > 3 && line.charAt(3) == '-') {
            line = in.readLine();
        }
    }

    private static class MailSettings {
        String host;
        int port;
        String username;
        String password;
        String from;
        boolean tls;
        boolean ssl;

        static MailSettings load() {
            MailSettings settings = new MailSettings();
            settings.host = value("library.smtp.host", "LIBRARY_SMTP_HOST", "");
            if (settings.host.isEmpty()) {
                throw new IllegalStateException("SMTP is not configured. Set LIBRARY_SMTP_HOST, LIBRARY_SMTP_PORT, LIBRARY_SMTP_FROM, and optional username/password.");
            }
            settings.port = Integer.parseInt(value("library.smtp.port", "LIBRARY_SMTP_PORT", "587"));
            settings.username = value("library.smtp.user", "LIBRARY_SMTP_USER", "");
            settings.password = value("library.smtp.password", "LIBRARY_SMTP_PASSWORD", "");
            settings.from = value("library.smtp.from", "LIBRARY_SMTP_FROM", settings.username);
            settings.tls = Boolean.parseBoolean(value("library.smtp.tls", "LIBRARY_SMTP_TLS", "true"));
            settings.ssl = Boolean.parseBoolean(value("library.smtp.ssl", "LIBRARY_SMTP_SSL", "false"));
            return settings;
        }

        private static String value(String property, String env, String defaultValue) {
            String systemValue = System.getProperty(property);
            if (systemValue != null && !systemValue.isBlank()) {
                return systemValue.trim();
            }
            String envValue = System.getenv(env);
            if (envValue != null && !envValue.isBlank()) {
                return envValue.trim();
            }
            return defaultValue;
        }
    }
}

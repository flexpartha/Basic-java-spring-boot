package com.example.userapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoginNotificationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public LoginNotificationService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Sends an HTML e‑mail to the supplied address with login‑attempt details.
     *
     * @param toEmail   recipient address
     * @param username  user name that attempted to log in
     * @param successful true if login succeeded, false otherwise
     * @param ip        IP address of the request (may be null)
     */
    public void sendLoginNotification(String toEmail,
                                       String username,
                                       boolean successful,
                                       String ip) throws MessagingException {
        // Build Thymeleaf context
        Context ctx = new Context();
        ctx.setVariable("username", username);
        ctx.setVariable("successful", successful);
        ctx.setVariable("ip", ip != null ? ip : "unknown");
        ctx.setVariable("timestamp", ZonedDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")));

        // Render template (file name without extension)
        String htmlBody = templateEngine.process("login-notification.html", ctx);

        // Prepare and send the e‑mail
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("Login attempt notification");
        helper.setText(htmlBody, true); // true = HTML

        mailSender.send(message);
    }
}

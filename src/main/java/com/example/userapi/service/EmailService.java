package com.example.userapi.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendLoginNotification(String name, String username, String email) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("username", username);
            context.setVariable("email", email);
            context.setVariable("loginTime",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

            String html = templateEngine.process("login-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Login Notification");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            // log and continue — don't fail login if email fails
            System.err.println("Failed to send login email: " + e.getMessage());
        }
    }
}

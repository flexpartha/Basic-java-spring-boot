package com.example.userapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "spring.mail.host")
public class LoginNotificationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public LoginNotificationService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendLoginNotification(String toEmail,
                                       String username,
                                       boolean successful,
                                       String ip) throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable("username", username);
        ctx.setVariable("successful", successful);
        ctx.setVariable("ip", ip != null ? ip : "unknown");
        ctx.setVariable("timestamp", ZonedDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")));

        String htmlBody = templateEngine.process("login-notification.html", ctx);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("Login attempt notification");
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}

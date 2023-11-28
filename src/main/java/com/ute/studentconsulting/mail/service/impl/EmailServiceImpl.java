package com.ute.studentconsulting.mail.service.impl;

import com.ute.studentconsulting.exception.InternalServerErrorException;
import com.ute.studentconsulting.mail.service.MailService;
import com.ute.studentconsulting.model.SimpleMailModel;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String sender;

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendSimpleMail(SimpleMailModel mail)  {
        var mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper
                    (mimeMessage, true, StandardCharsets.UTF_8.name());
        } catch (MessagingException e) {
            throw new InternalServerErrorException("Lỗi server", e.getMessage(), 10066);
        }
        var context = new Context();
        context.setVariable("resetUrl", mail.getUrl());
        var htmlContent = templateEngine.process("emailTemplate", context);
        try {
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(mail.getRecipient());
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setSubject(mail.getSubject());
        } catch (MessagingException e) {
            throw new InternalServerErrorException("Lỗi server", e.getMessage(), 10067);
        }
        javaMailSender.send(mimeMessage);
    }
}

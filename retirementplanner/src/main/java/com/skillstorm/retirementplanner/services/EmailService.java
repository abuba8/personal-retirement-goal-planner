package com.skillstorm.retirementplanner.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    /**
     * EmailService:
     * Wrapper class built around JavaMailSender for sending HTML emails.
     * Keeps the raw mail plumbing out of the business logic in AuthService.
     *
     * Methods:
     * - sendVerificationEmail(to, subject, htmlBody): sends one HTML email
     */
    private final JavaMailSender sender;

    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    /**
     * sendVerificationEmail:
     * Sends a single HTML email.
     *
     * args:
     * - String to: recipient email address
     * - String subject: the email subject line
     * - String htmlBody: the HTML content of the message
     *
     * throws:
     * - MessagingException: if the message can't be built or sent
     */
    public void sendVerificationEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage msg = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        sender.send(msg);

    }
}

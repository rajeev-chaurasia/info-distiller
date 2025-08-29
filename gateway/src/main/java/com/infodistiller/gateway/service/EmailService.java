package com.infodistiller.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlBody = "<h1>Your Info-Distiller Login Code</h1>"
                    + "<p>Please use the following code to log in:</p>"
                    + "<h2>" + otp + "</h2>"
                    + "<p>This code will expire in 10 minutes.</p>";

            helper.setText(htmlBody, true);
            helper.setTo(toEmail);
            helper.setSubject("Your Info-Distiller Login Code");

            mailSender.send(mimeMessage);
            System.out.println("OTP Email sent successfully to " + toEmail);

        } catch (Exception e) {
            System.err.println("Error sending OTP email: " + e.getMessage());
        }
    }
}

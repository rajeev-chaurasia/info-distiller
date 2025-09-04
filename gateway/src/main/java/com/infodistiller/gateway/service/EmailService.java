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

    public void sendBriefingReadyEmail(String toEmail, String briefingUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlBody = "<h1>Your Info-Distiller Briefing is Ready!</h1>"
                    + "<p>Your personalized AI-powered news summary has been generated.</p>"
                    + "<a href=\"" + briefingUrl + "\" "
                    + "style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #2563eb; text-decoration: none; border-radius: 5px;\">"
                    + "View Your Briefing"
                    + "</a>";

            helper.setText(htmlBody, true);
            helper.setTo(toEmail);
            helper.setSubject("Your Daily Briefing is Ready");

            mailSender.send(mimeMessage);
            System.out.println("Briefing ready notification sent successfully to " + toEmail);

        } catch (Exception e) {
            System.err.println("Error sending briefing ready email: " + e.getMessage());
        }
    }
}

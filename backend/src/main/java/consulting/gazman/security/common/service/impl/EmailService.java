package consulting.gazman.security.common.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromAddress;
    @Value("${app.base-url}")
    private String baseUrl;
    public void sendVerificationEmail(String to, String token) {
        String verificationLink = baseUrl + "/api/auth/verify-email?token=" + token;

        // Email content
        String subject = "Verify Your Email";
        String message = "Hello,\n\n"
                + "Please verify your email by clicking the link below:\n\n"
                + verificationLink + "\n\n"
                + "Thank you!";

        sendEmail(to, subject, message);
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

        System.out.println("Email sent:" + message);
        } catch (MailException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}

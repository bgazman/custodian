package consulting.gazman.security.service.impl;

import consulting.gazman.security.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendSms(String phoneNumber, String message) {
        // Log the SMS sending for debugging
        System.out.println("Sending SMS to: " + phoneNumber + " | Message: " + message);

        // Future integration example:
        // Twilio.init(accountSid, authToken);
        // Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(fromNumber), message).create();
    }

    @Override
    public void sendEmail(String email, String subject, String message) {
        // Log the email sending for debugging
        System.out.println("Sending Email to: " + email + " | Subject: " + subject + " | Message: " + message);

        // Future integration example with JavaMailSender:
        // MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        // MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        // helper.setTo(email);
        // helper.setSubject(subject);
        // helper.setText(message, true);
        // javaMailSender.send(mimeMessage);
    }
}

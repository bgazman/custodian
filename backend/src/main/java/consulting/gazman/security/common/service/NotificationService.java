package consulting.gazman.security.common.service;

public interface NotificationService {
    /**
     * Sends an SMS to the specified phone number with the given message.
     *
     * @param phoneNumber the recipient's phone number
     * @param message     the message to be sent
     */
    void sendSms(String phoneNumber, String message);

    /**
     * Sends an email to the specified email address with the given subject and message.
     *
     * @param email   the recipient's email address
     * @param subject the subject of the email
     * @param message the body of the email
     */
    void sendEmail(String email, String subject, String message);
}


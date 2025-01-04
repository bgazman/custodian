package consulting.gazman.common.dto;

import consulting.gazman.common.constants.NotificationChannel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    private String to;           // Recipient's address (email/phone)
    private String subject;      // Subject (for email)
    private String message;      // Message body
    private NotificationChannel channel; // Enum for channel (EMAIL, SMS)
}

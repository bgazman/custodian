package common.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private String message;
    private Object details;



    // Getters and setters
}

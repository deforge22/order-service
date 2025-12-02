package io.dinesync.orderstream.rest.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> errors;

    public static ValidationErrorResponse of(String message, String path, Map<String, String> errors) {
        return ValidationErrorResponse.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .errors(errors)
                .build();
    }

}

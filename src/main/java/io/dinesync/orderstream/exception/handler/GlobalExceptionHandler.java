package io.dinesync.orderstream.exception.handler;

import io.dinesync.orderstream.exception.OrderNotFoundException;
import io.dinesync.orderstream.rest.model.response.ApiResponse;
import io.dinesync.orderstream.rest.model.response.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
@NullMarked
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            var fieldName = ((FieldError) error).getField();
            var errorMsg = error.getDefaultMessage();
            errors.put(fieldName, Objects.isNull(errorMsg) || errorMsg.isEmpty() ? "No message" : errorMsg);
        });
        log.error("Validation failed for request: {} - Errors: {}", request.getRequestURI(), errors);

        var response = ValidationErrorResponse.of(
                "Validation failed",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        log.error("MethodArgumentTypeMismatch for request: {} - Errors: {}", request.getRequestURI(), ex.getMessage());
        var message = ex.getMessage();
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) ex.getRequiredType();
            String[] validValues = Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .toArray(String[]::new);

            message = String.format(
                    "Invalid value '%s' for parameter '%s'. Accepted values are: %s",
                    ex.getValue(),
                    ex.getName(),
                    String.join(", ", validValues)
            );
        }
        ApiResponse<Void> response = ApiResponse.error(message);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        log.error("ConstraintViolationException for request: {} - Errors: {}",
                request.getRequestURI(), ex.getMessage());

        // Extract the first constraint violation message
        String errorMessage = ex.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Validation failed");

        ApiResponse<Void> response = ApiResponse.error(errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.error("Illegal argument: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrderNotFoundException(
            OrderNotFoundException ex,
            HttpServletRequest request) {

        log.error("OrderNotFoundException thrown for order with id: {} - Path: {}", ex.getOrderId(), request.getRequestURI());

        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex,
            HttpServletRequest request
    ) {
        log.error("ValidationException for request: {} - Error: {}",
                request.getRequestURI(), ex.getMessage(), ex);


        ApiResponse<Void> response = ApiResponse.error(
                "Validation failed: " + ex.getMessage()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleHandlerMethodValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest request
    ) {
        log.error("HandlerMethodValidationException for request: {} - Errors: {}",
                request.getRequestURI(), ex.getMessage());

        String errorMessage = ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .findFirst()
                .map(error -> {
                    String message = error.getDefaultMessage();
                    if (message != null && !message.isBlank()) {
                        return message;
                    }String[] codes = error.getCodes();
                    if (codes.length > 0) {
                        return codes[0];
                    }
                    return "Validation failed";
                })
                .orElse("Validation failed");

        ApiResponse<Void> response = ApiResponse.error(errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error occurred - Path: {} - Error: {}",
                request.getRequestURI(), ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error(
                "An unexpected error occurred. Please try again later."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}

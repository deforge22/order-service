package io.dinesync.orderstream.utility.generator;


import io.dinesync.orderstream.rest.model.response.ApiResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@NullMarked
public class ApiResponseGenerator {

    private ApiResponseGenerator() {}

    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return created(data, "Resource created successfully");
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity
                .ok()
                .body(ApiResponse.success(data, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ok(data, "Operation successful");
    }

    public static ResponseEntity<ApiResponse<?>> deleted(String message) {
        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, message));
    }

    public static ResponseEntity<ApiResponse<?>> deleted() {
        return deleted("Resource deleted successfully");
    }


    public static ResponseEntity<ApiResponse<?>> error(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(message));
    }

    public static ResponseEntity<ApiResponse<?>> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    public static ResponseEntity<ApiResponse<?>> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    public static ResponseEntity<ApiResponse<?>> internalError(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}

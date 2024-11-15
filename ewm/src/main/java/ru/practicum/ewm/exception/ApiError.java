package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private String message;
    private String reason;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiError(HttpStatus status, String message, String reason) {
        this.status = status.name();
        this.reason = reason;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
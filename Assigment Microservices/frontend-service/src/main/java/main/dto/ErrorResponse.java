package main.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorResponse {

    private String message;
    private String errorCode;
    private String timestamp;
    private int status;

    public ErrorResponse() {}

    public ErrorResponse(String message, String errorCode, int status) {
        this.message   = message;
        this.errorCode = errorCode;
        this.status    = status;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getMessage()   { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public int getStatus()       { return status; }
    public void setStatus(int status) { this.status = status; }
}
package com.miniwallet.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;

public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> details;

    public ErrorResponse() {}

    public ErrorResponse(String timestamp, int status, String error, String message, Map<String, String> details) {
        this.timestamp = timestamp; this.status = status; this.error = error;
        this.message = message; this.details = details;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, String> getDetails() { return details; }
    public void setDetails(Map<String, String> details) { this.details = details; }
}
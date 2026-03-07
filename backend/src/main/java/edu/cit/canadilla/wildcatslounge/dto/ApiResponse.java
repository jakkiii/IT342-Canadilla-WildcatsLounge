package edu.cit.canadilla.wildcatslounge.dto;

import lombok.Data;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ApiResponse {

    private boolean success;
    private Object data;
    private String error;
    private String timestamp;

    public ApiResponse(boolean success, Object data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.timestamp = ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    public static ApiResponse success(Object data) {
        return new ApiResponse(true, data, null);
    }

    public static ApiResponse error(String error) {
        return new ApiResponse(false, null, error);
    }
}

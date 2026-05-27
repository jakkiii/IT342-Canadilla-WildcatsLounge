package edu.cit.canadilla.wildcatslounge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private String postLink;
    @NotNull(message = "Start datetime is required")
    private LocalDateTime startDatetime;
    @NotNull(message = "End datetime is required")
    private LocalDateTime endDatetime;
}

package edu.cit.canadilla.wildcatslounge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OrderStatusRequest {
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(pending|preparing|ready|completed)$", message = "Invalid status")
    private String status;
}

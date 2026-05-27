package edu.cit.canadilla.wildcatslounge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoungeStatusRequest {
    @NotBlank(message = "Occupancy level is required")
    @Pattern(regexp = "^(low|medium|full|closed)$", message = "Occupancy must be low, medium, full, or closed")
    private String occupancyLevel;
}

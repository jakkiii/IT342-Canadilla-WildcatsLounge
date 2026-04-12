package edu.cit.canadilla.wildcatslounge.dto;

import edu.cit.canadilla.wildcatslounge.entity.ServingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequest {

    @NotNull(message = "Menu item is required")
    private Long menuItemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Size(max = 255, message = "Customization notes must not exceed 255 characters")
    private String customizationNotes;

    private ServingType servingType;
}

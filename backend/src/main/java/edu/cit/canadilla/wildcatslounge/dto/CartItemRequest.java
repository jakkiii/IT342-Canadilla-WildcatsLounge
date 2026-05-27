package edu.cit.canadilla.wildcatslounge.dto;

import edu.cit.canadilla.wildcatslounge.common.ServingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CartItemRequest {
    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;
    private String customizationNotes;
    private ServingType servingType;
    private Integer sugarLevelPercent;
    private List<Long> addonIds;
}

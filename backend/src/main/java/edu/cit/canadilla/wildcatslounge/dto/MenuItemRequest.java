package edu.cit.canadilla.wildcatslounge.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @NotBlank(message = "Category is required")
    private String category;
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    private Boolean isAvailable = true;
    private String imageUrl;
    private Boolean allowHot;
    private Boolean allowIced;
    private Boolean allowBlended;
    private Boolean allowAddons;
    private Boolean allowSugarLevel;
}

package edu.cit.canadilla.wildcatslounge.feature.cart.dto;

import edu.cit.canadilla.wildcatslounge.feature.menu.entity.MenuCategory;
import edu.cit.canadilla.wildcatslounge.common.ServingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long menuItemId;
    private String itemName;
    private MenuCategory category;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String customizationNotes;
    private ServingType servingType;
    private String imageUrl;
    private BigDecimal lineTotal;
}

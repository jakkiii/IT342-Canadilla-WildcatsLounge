package edu.cit.canadilla.wildcatslounge.feature.order.dto;

import edu.cit.canadilla.wildcatslounge.common.ServingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long menuItemId;
    private String itemName;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private String customizationNotes;
    private ServingType servingType;
    private BigDecimal lineTotal;
}

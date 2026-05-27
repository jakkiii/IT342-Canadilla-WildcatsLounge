package edu.cit.canadilla.wildcatslounge.dto;

import edu.cit.canadilla.wildcatslounge.entity.Ingredient;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredientResponse {
    private Long id;
    private String name;
    private String unit;
    private BigDecimal quantityOnHand;
    private BigDecimal lowStockThreshold;
    private boolean lowStock;
    private boolean outOfStock;

    public static IngredientResponse from(Ingredient ingredient) {
        IngredientResponse r = new IngredientResponse();
        r.setId(ingredient.getId());
        r.setName(ingredient.getName());
        r.setUnit(ingredient.getUnit());
        r.setQuantityOnHand(ingredient.getQuantityOnHand());
        r.setLowStockThreshold(ingredient.getLowStockThreshold());
        BigDecimal qty = ingredient.getQuantityOnHand() != null ? ingredient.getQuantityOnHand() : BigDecimal.ZERO;
        BigDecimal threshold = ingredient.getLowStockThreshold() != null
                ? ingredient.getLowStockThreshold()
                : BigDecimal.ZERO;
        r.setOutOfStock(qty.compareTo(BigDecimal.ZERO) <= 0);
        r.setLowStock(!r.isOutOfStock() && qty.compareTo(threshold) <= 0);
        return r;
    }
}

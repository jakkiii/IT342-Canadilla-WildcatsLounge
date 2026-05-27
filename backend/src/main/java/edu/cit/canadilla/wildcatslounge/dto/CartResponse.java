package edu.cit.canadilla.wildcatslounge.dto;

import edu.cit.canadilla.wildcatslounge.entity.Cart;
import edu.cit.canadilla.wildcatslounge.entity.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CartResponse {
    private Long id;
    private Long cartId;
    private List<CartItemResponse> items;
    private BigDecimal subtotal;

    @Data
    public static class CartItemResponse {
        private Long id;
        private Long menuItemId;
        private String itemName;
        private BigDecimal unitPrice;
        private Integer quantity;
        private String customizationNotes;
        private Long parentItemId;
        private String servingType;
        private Integer sugarLevelPercent;
        private BigDecimal lineTotal;
    }

    public static CartResponse from(Cart cart) {
        CartResponse r = new CartResponse();
        r.setId(cart.getId());
        // cartId alias for clients that expect it
        r.setCartId(cart.getId());
        r.setItems(cart.getItems().stream().map(item -> {
            CartItemResponse ci = new CartItemResponse();
            ci.setId(item.getId());
            ci.setMenuItemId(item.getMenuItem().getId());
            ci.setItemName(item.getMenuItem().getName());
            ci.setUnitPrice(item.getMenuItem().getPrice());
            ci.setQuantity(item.getQuantity());
            ci.setCustomizationNotes(item.getCustomizationNotes());
            ci.setParentItemId(item.getParentItem() != null ? item.getParentItem().getId() : null);
            ci.setServingType(item.getServingType() != null ? item.getServingType().name() : null);
            ci.setSugarLevelPercent(item.getSugarLevelPercent());
            ci.setLineTotal(item.getMenuItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return ci;
        }).collect(Collectors.toList()));
        r.setSubtotal(r.getItems().stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return r;
    }
}

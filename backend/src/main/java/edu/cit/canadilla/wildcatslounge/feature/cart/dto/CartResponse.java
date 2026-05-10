package edu.cit.canadilla.wildcatslounge.feature.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private Long userId;
    private Integer itemCount;
    private BigDecimal subtotal;
    private LocalDateTime updatedAt;
    private List<CartItemResponse> items;
}

package edu.cit.canadilla.wildcatslounge.dto;

import edu.cit.canadilla.wildcatslounge.entity.Order;
import edu.cit.canadilla.wildcatslounge.entity.OrderItem;
import edu.cit.canadilla.wildcatslounge.entity.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String customerName;
    private String customerEmail;
    private String customerStudentId;
    private List<OrderItemResponse> items;

    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long menuItemId;
        private String itemName;
        private Integer quantity;
        private BigDecimal priceAtPurchase;
        private String customizationNotes;
        private Long parentItemId;
        private String servingType;
        private Integer sugarLevelPercent;
    }

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setOrderNumber(order.getOrderNumber());
        r.setStatus(order.getStatus());
        r.setTotalAmount(order.getTotalAmount());
        r.setCreatedAt(order.getCreatedAt());
        r.setUpdatedAt(order.getUpdatedAt());
        User u = order.getUser();
        if (u != null) {
            r.setCustomerName(u.getFirstname() + " " + u.getLastname());
            r.setCustomerEmail(u.getEmail());
            r.setCustomerStudentId(u.getStudentId());
        }
        r.setItems(order.getItems().stream().map(oi -> {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setId(oi.getId());
            ir.setMenuItemId(oi.getMenuItemId());
            ir.setItemName(oi.getItemName());
            ir.setQuantity(oi.getQuantity());
            ir.setPriceAtPurchase(oi.getPriceAtPurchase());
            ir.setCustomizationNotes(oi.getCustomizationNotes());
            ir.setParentItemId(oi.getParentItem() != null ? oi.getParentItem().getId() : null);
            ir.setServingType(oi.getServingType() != null ? oi.getServingType().name() : null);
            ir.setSugarLevelPercent(oi.getSugarLevelPercent());
            return ir;
        }).collect(Collectors.toList()));
        return r;
    }
}

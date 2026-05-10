package edu.cit.canadilla.wildcatslounge.feature.order.service;

import edu.cit.canadilla.wildcatslounge.feature.order.dto.OrderItemResponse;
import edu.cit.canadilla.wildcatslounge.feature.order.dto.OrderResponse;
import edu.cit.canadilla.wildcatslounge.feature.cart.entity.Cart;
import edu.cit.canadilla.wildcatslounge.feature.cart.entity.CartItem;
import edu.cit.canadilla.wildcatslounge.feature.cart.service.CartService;
import edu.cit.canadilla.wildcatslounge.feature.order.entity.Order;
import edu.cit.canadilla.wildcatslounge.feature.order.entity.OrderItem;
import edu.cit.canadilla.wildcatslounge.feature.order.entity.OrderStatus;
import edu.cit.canadilla.wildcatslounge.feature.order.repository.OrderItemRepository;
import edu.cit.canadilla.wildcatslounge.feature.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final Random random = new Random();

    @Transactional
    public OrderResponse checkout(Long userId) {
        Cart cart = cartService.getOrCreateCart(userId);
        List<CartItem> cartItems = cartService.getCartItems(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cannot checkout an empty cart");
        }

        BigDecimal totalAmount = cartItems.stream()
            .map(item -> cartService.resolveUnitPrice(item.getMenuItem(), item.getServingType())
                .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setItemName(cartItem.getMenuItem().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartService.resolveUnitPrice(cartItem.getMenuItem(), cartItem.getServingType()));
            orderItem.setCustomizationNotes(cartItem.getCustomizationNotes());
            orderItem.setServingType(cartItem.getServingType());
            return orderItem;
        }).toList();

        orderItemRepository.saveAll(orderItems);
        cartService.clearCart(userId);

        return toOrderResponse(savedOrder, orderItems);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId());
            return toOrderResponse(order, items);
        }).toList();
    }

    private OrderResponse toOrderResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream().map(item -> new OrderItemResponse(
                item.getId(),
                item.getMenuItem().getId(),
                item.getItemName(),
                item.getQuantity(),
                item.getPriceAtPurchase(),
                item.getCustomizationNotes(),
                item.getServingType(),
                item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))
        )).toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                itemResponses
        );
    }

    private String generateOrderNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        String orderNumber;
        do {
            int suffix = 100 + random.nextInt(900);
            orderNumber = "ORD-" + LocalDateTime.now().format(formatter) + "-" + suffix;
        } while (orderRepository.existsByOrderNumber(orderNumber));

        return orderNumber;
    }
}

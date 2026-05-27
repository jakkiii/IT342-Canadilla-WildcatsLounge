package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.common.OrderStatus;
import edu.cit.canadilla.wildcatslounge.common.MenuCustomizationRules;
import edu.cit.canadilla.wildcatslounge.common.ServingType;
import edu.cit.canadilla.wildcatslounge.dto.OrderResponse;
import edu.cit.canadilla.wildcatslounge.dto.StaffOrderAnalyticsResponse;
import edu.cit.canadilla.wildcatslounge.entity.*;
import edu.cit.canadilla.wildcatslounge.repository.MenuItemRepository;
import edu.cit.canadilla.wildcatslounge.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final MenuItemRepository menuItemRepository;

    @Transactional
    public OrderResponse placeOrder(User user) {
        Cart cart = cartService.getOrCreateCartEntity(user);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setStatus(OrderStatus.normalize("pending"));

        BigDecimal total = BigDecimal.ZERO;
        List<CartItem> rootItems = cart.getItems().stream()
                .filter(ci -> ci.getParentItem() == null)
                .toList();

        for (CartItem root : rootItems) {
            if (!Boolean.TRUE.equals(root.getMenuItem().getIsAvailable())) {
                throw new RuntimeException(root.getMenuItem().getName() + " is out of stock");
            }

            OrderItem parentOrderItem = toOrderItem(order, root, null);
            order.getItems().add(parentOrderItem);
            total = total.add(root.getMenuItem().getPrice().multiply(BigDecimal.valueOf(root.getQuantity())));

            for (CartItem child : cart.getItems().stream()
                    .filter(ci -> ci.getParentItem() != null && ci.getParentItem().getId().equals(root.getId()))
                    .toList()) {
                if (!Boolean.TRUE.equals(child.getMenuItem().getIsAvailable())) {
                    throw new RuntimeException(child.getMenuItem().getName() + " is out of stock");
                }
                OrderItem addonOrderItem = toOrderItem(order, child, parentOrderItem);
                order.getItems().add(addonOrderItem);
                total = total.add(child.getMenuItem().getPrice().multiply(BigDecimal.valueOf(child.getQuantity())));
            }
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        cartService.clearCart(user);
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(User user) {
        return orderRepository.findByUserWithDetails(user.getId()).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id, User user, boolean staff) {
        Order order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!staff && !order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrdersForStaff() {
        return orderRepository.findActiveWithDetails(
                Arrays.asList("pending", "preparing", "ready")).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countPendingOrders() {
        return orderRepository.countByStatusInIgnoreCase(Arrays.asList("pending", "preparing", "ready"));
    }

    @Transactional
    public OrderResponse updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        try {
            order.setStatus(OrderStatus.normalize(status));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            return OrderResponse.from(orderRepository.save(order));
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Could not update order status. Please restart the backend and try again.");
        }
    }

    @Transactional(readOnly = true)
    public StaffOrderAnalyticsResponse getStaffOrderAnalytics() {
        List<Order> orders = orderRepository.findAllWithItems();
        LocalDate today = LocalDate.now();
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

        LocalDate dailyStart = today.minusDays(13);
        Map<LocalDate, Long> dailyCounts = orders.stream()
                .map(o -> o.getCreatedAt().toLocalDate())
                .filter(d -> !d.isBefore(dailyStart) && !d.isAfter(today))
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));
        Map<LocalDate, BigDecimal> dailyRevenue = orders.stream()
                .filter(o -> {
                    LocalDate d = o.getCreatedAt().toLocalDate();
                    return !d.isBefore(dailyStart) && !d.isAfter(today);
                })
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)));
        List<StaffOrderAnalyticsResponse.TrendPoint> daily = dailyStart.datesUntil(today.plusDays(1))
                .map(d -> new StaffOrderAnalyticsResponse.TrendPoint(
                        d.format(dayFmt),
                        dailyCounts.getOrDefault(d, 0L),
                        dailyRevenue.getOrDefault(d, BigDecimal.ZERO)))
                .toList();

        LocalDate thisWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weeklyStart = thisWeek.minusWeeks(7);
        Map<LocalDate, Long> weeklyCounts = orders.stream()
                .map(o -> o.getCreatedAt().toLocalDate().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)))
                .filter(d -> !d.isBefore(weeklyStart) && !d.isAfter(thisWeek))
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));
        Map<LocalDate, BigDecimal> weeklyRevenue = orders.stream()
                .filter(o -> {
                    LocalDate weekStart = o.getCreatedAt().toLocalDate()
                            .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                    return !weekStart.isBefore(weeklyStart) && !weekStart.isAfter(thisWeek);
                })
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().toLocalDate()
                                .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)));
        List<StaffOrderAnalyticsResponse.TrendPoint> weekly = weeklyStart
                .datesUntil(thisWeek.plusWeeks(1), java.time.Period.ofWeeks(1))
                .map(weekStart -> new StaffOrderAnalyticsResponse.TrendPoint(
                        "Week of " + weekStart.format(dayFmt),
                        weeklyCounts.getOrDefault(weekStart, 0L),
                        weeklyRevenue.getOrDefault(weekStart, BigDecimal.ZERO)))
                .toList();

        LocalDate thisMonth = today.withDayOfMonth(1);
        LocalDate monthlyStart = thisMonth.minusMonths(5);
        Map<LocalDate, Long> monthlyCounts = orders.stream()
                .map(o -> o.getCreatedAt().toLocalDate().withDayOfMonth(1))
                .filter(d -> !d.isBefore(monthlyStart) && !d.isAfter(thisMonth))
                .collect(Collectors.groupingBy(d -> d, Collectors.counting()));
        Map<LocalDate, BigDecimal> monthlyRevenue = orders.stream()
                .filter(o -> {
                    LocalDate monthStart = o.getCreatedAt().toLocalDate().withDayOfMonth(1);
                    return !monthStart.isBefore(monthlyStart) && !monthStart.isAfter(thisMonth);
                })
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().toLocalDate().withDayOfMonth(1),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)));
        List<StaffOrderAnalyticsResponse.TrendPoint> monthly = monthlyStart
                .datesUntil(thisMonth.plusMonths(1), java.time.Period.ofMonths(1))
                .map(monthStart -> new StaffOrderAnalyticsResponse.TrendPoint(
                        monthStart.format(monthFmt),
                        monthlyCounts.getOrDefault(monthStart, 0L),
                        monthlyRevenue.getOrDefault(monthStart, BigDecimal.ZERO)))
                .toList();

        List<MenuItem> allMenuItems = menuItemRepository.findAll();
        Map<Long, MenuItem> menuItemsById = allMenuItems.stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));
        Map<String, MenuItem> menuItemsByName = allMenuItems.stream()
                .collect(Collectors.toMap(
                        item -> normalizeItemName(item.getName()),
                        Function.identity(),
                        (first, second) -> first));

        Map<String, ItemAccumulator> itemStats = new LinkedHashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                MenuItem menuItem = item.getMenuItemId() == null ? null : menuItemsById.get(item.getMenuItemId());
                if (menuItem == null) {
                    menuItem = menuItemsByName.get(normalizeItemName(item.getItemName()));
                }
                if (item.getParentItem() != null || (menuItem != null && MenuCustomizationRules.isAddonCategory(menuItem.getCategory()))) {
                    continue;
                }
                ItemAccumulator acc = itemStats.computeIfAbsent(item.getItemName(), k -> new ItemAccumulator());
                acc.quantity += item.getQuantity();
                acc.orderCount += 1;
            }
        }

        List<StaffOrderAnalyticsResponse.TopItemPoint> topItems = itemStats.entrySet().stream()
                .map(e -> new StaffOrderAnalyticsResponse.TopItemPoint(
                        e.getKey(),
                        e.getValue().quantity,
                        e.getValue().orderCount))
                .sorted(Comparator.comparingLong(StaffOrderAnalyticsResponse.TopItemPoint::getQuantity).reversed())
                .limit(3)
                .toList();

        List<StaffOrderAnalyticsResponse.StatusPoint> statusDistribution = Arrays.asList(
                "pending", "preparing", "ready", "completed").stream()
                .map(status -> new StaffOrderAnalyticsResponse.StatusPoint(
                        status,
                        orders.stream().filter(o -> status.equalsIgnoreCase(o.getStatus())).count()))
                .toList();

        return new StaffOrderAnalyticsResponse(daily, weekly, monthly, topItems, statusDistribution);
    }

    private OrderItem toOrderItem(Order order, CartItem cartItem, OrderItem parentItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setParentItem(parentItem);
        orderItem.setMenuItemId(cartItem.getMenuItem().getId());
        orderItem.setItemName(cartItem.getMenuItem().getName());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPriceAtPurchase(cartItem.getMenuItem().getPrice());
        orderItem.setCustomizationNotes(sanitizeNotes(cartItem.getCustomizationNotes()));
        orderItem.setServingType(cartItem.getServingType() != null ? cartItem.getServingType() : ServingType.NONE);
        orderItem.setSugarLevelPercent(cartItem.getSugarLevelPercent());
        return orderItem;
    }

    private String sanitizeNotes(String notes) {
        if (notes == null) {
            return null;
        }
        String trimmed = notes.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeItemName(String itemName) {
        return itemName == null ? "" : itemName.trim().toLowerCase(Locale.ENGLISH);
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = orderRepository.count() + 1;
        return String.format("WL-%s-%03d", date, count);
    }

    private static class ItemAccumulator {
        long quantity;
        long orderCount;
    }
}

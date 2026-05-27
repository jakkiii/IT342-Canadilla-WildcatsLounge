package edu.cit.canadilla.wildcatslounge.config;

import edu.cit.canadilla.wildcatslounge.common.MenuCustomizationRules;
import edu.cit.canadilla.wildcatslounge.common.ServingType;
import edu.cit.canadilla.wildcatslounge.entity.Cart;
import edu.cit.canadilla.wildcatslounge.entity.CartItem;
import edu.cit.canadilla.wildcatslounge.entity.MenuItem;
import edu.cit.canadilla.wildcatslounge.entity.Order;
import edu.cit.canadilla.wildcatslounge.entity.OrderItem;
import edu.cit.canadilla.wildcatslounge.repository.CartRepository;
import edu.cit.canadilla.wildcatslounge.repository.MenuItemRepository;
import edu.cit.canadilla.wildcatslounge.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuCustomizationMigration {

    private final JdbcTemplate jdbcTemplate;
    private final MenuItemRepository menuItemRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void migrateMenuCustomizationData() {
        ensureColumns();
        syncMenuRules();
        backfillCartAddonParents();
        backfillOrderAddonParents();
    }

    private void ensureColumns() {
        try {
            jdbcTemplate.execute("ALTER TABLE menu_items ADD COLUMN IF NOT EXISTS allow_hot BOOLEAN DEFAULT FALSE");
            jdbcTemplate.execute("ALTER TABLE menu_items ADD COLUMN IF NOT EXISTS allow_iced BOOLEAN DEFAULT FALSE");
            jdbcTemplate.execute("ALTER TABLE menu_items ADD COLUMN IF NOT EXISTS allow_blended BOOLEAN DEFAULT FALSE");
            jdbcTemplate.execute("ALTER TABLE menu_items ADD COLUMN IF NOT EXISTS allow_addons BOOLEAN DEFAULT FALSE");
            jdbcTemplate.execute("ALTER TABLE menu_items ADD COLUMN IF NOT EXISTS allow_sugar_level BOOLEAN DEFAULT FALSE");

            jdbcTemplate.execute("ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS parent_cart_item_id BIGINT");
            jdbcTemplate.execute("ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS sugar_level_percent INTEGER");

            jdbcTemplate.execute("ALTER TABLE order_items ADD COLUMN IF NOT EXISTS parent_order_item_id BIGINT");
            jdbcTemplate.execute("ALTER TABLE order_items ADD COLUMN IF NOT EXISTS serving_type VARCHAR(20)");
            jdbcTemplate.execute("ALTER TABLE order_items ADD COLUMN IF NOT EXISTS sugar_level_percent INTEGER");
        } catch (Exception e) {
            log.warn("Customization column migration skipped: {}", e.getMessage());
        }
    }

    private void syncMenuRules() {
        List<MenuItem> items = menuItemRepository.findAll();
        int updated = 0;
        for (MenuItem item : items) {
            Boolean beforeHot = item.getAllowHot();
            Boolean beforeIced = item.getAllowIced();
            Boolean beforeBlended = item.getAllowBlended();
            Boolean beforeAddons = item.getAllowAddons();
            Boolean beforeSugar = item.getAllowSugarLevel();

            MenuCustomizationRules.syncToDefaults(item);

            if (!equalsBool(beforeHot, item.getAllowHot())
                    || !equalsBool(beforeIced, item.getAllowIced())
                    || !equalsBool(beforeBlended, item.getAllowBlended())
                    || !equalsBool(beforeAddons, item.getAllowAddons())
                    || !equalsBool(beforeSugar, item.getAllowSugarLevel())) {
                updated++;
            }
        }
        if (updated > 0) {
            menuItemRepository.saveAll(items);
            log.info("Updated customization rules for {} menu item(s)", updated);
        }
    }

    private void backfillCartAddonParents() {
        int updated = 0;
        List<Cart> carts = cartRepository.findAllWithItems();
        for (Cart cart : carts) {
            CartItem lastBase = null;
            List<CartItem> items = cart.getItems().stream()
                    .sorted(Comparator.comparing(CartItem::getId))
                    .toList();

            for (CartItem item : items) {
                boolean isAddon = MenuCustomizationRules.isAddonCategory(item.getMenuItem().getCategory());
                if (isAddon && item.getParentItem() == null && lastBase != null) {
                    item.setParentItem(lastBase);
                    item.setServingType(ServingType.NONE);
                    item.setSugarLevelPercent(null);
                    updated++;
                    continue;
                }
                if (!isAddon) {
                    lastBase = item;
                }
            }
        }

        if (updated > 0) {
            cartRepository.saveAll(carts);
            log.info("Linked {} cart add-on item(s) to their parent drinks", updated);
        }
    }

    private void backfillOrderAddonParents() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        Map<Long, MenuItem> byId = menuItems.stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity(), (first, second) -> first));
        Map<String, MenuItem> byName = menuItems.stream()
                .collect(Collectors.toMap(
                        item -> item.getName().trim().toLowerCase(),
                        Function.identity(),
                        (first, second) -> first));

        int updated = 0;
        List<Order> orders = orderRepository.findAllWithItems();
        for (Order order : orders) {
            OrderItem lastBase = null;
            List<OrderItem> items = order.getItems().stream()
                    .sorted(Comparator.comparing(OrderItem::getId))
                    .toList();

            for (OrderItem item : items) {
                MenuItem menuItem = item.getMenuItemId() == null ? null : byId.get(item.getMenuItemId());
                if (menuItem == null && item.getItemName() != null) {
                    menuItem = byName.get(item.getItemName().trim().toLowerCase());
                }
                boolean isAddon = menuItem != null && MenuCustomizationRules.isAddonCategory(menuItem.getCategory());
                if (isAddon && item.getParentItem() == null && lastBase != null) {
                    item.setParentItem(lastBase);
                    item.setServingType(ServingType.NONE);
                    item.setSugarLevelPercent(null);
                    updated++;
                    continue;
                }
                if (!isAddon) {
                    lastBase = item;
                }
            }
        }

        if (updated > 0) {
            orderRepository.saveAll(orders);
            log.info("Linked {} order add-on item(s) to their parent drinks", updated);
        }
    }

    private boolean equalsBool(Boolean a, Boolean b) {
        return Boolean.TRUE.equals(a) == Boolean.TRUE.equals(b);
    }
}

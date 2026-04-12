package edu.cit.canadilla.wildcatslounge.config;

import edu.cit.canadilla.wildcatslounge.entity.MenuCategory;
import edu.cit.canadilla.wildcatslounge.entity.MenuItem;
import edu.cit.canadilla.wildcatslounge.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class MenuSeedConfig {

    private final MenuItemRepository menuItemRepository;

    @Bean
    @Order(0)
    CommandLineRunner alignServingTypeColumns(JdbcTemplate jdbcTemplate) {
        return args -> {
            alignServingTypeColumn(jdbcTemplate, "cart_items");
            alignServingTypeColumn(jdbcTemplate, "order_items");
        };
    }

    @Bean
    @Order(1)
    CommandLineRunner alignMenuCategoryConstraint(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("ALTER TABLE menu_items DROP CONSTRAINT IF EXISTS menu_items_category_check");
            jdbcTemplate.execute("""
                    ALTER TABLE menu_items
                    ADD CONSTRAINT menu_items_category_check
                    CHECK (category IN (
                        'COFFEE',
                        'FLAVORED_LATTE',
                        'MATCHA_SERIES',
                        'BEVERAGES',
                        'COFFEE_ADD_ON',
                        'NON_COFFEE',
                        'TREAT'
                    ))
                    """);
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner seedMenuItems() {
        return args -> {
            List<MenuItemSpec> specs = buildMenuSpecs();

            Map<String, MenuItem> existingByName = new LinkedHashMap<>();
            for (MenuItem existing : menuItemRepository.findAll()) {
                existing.setIsAvailable(false);
                existingByName.put(existing.getName().toLowerCase(), existing);
            }

            List<MenuItem> toSave = new ArrayList<>();
            for (MenuItemSpec spec : specs) {
                MenuItem item = existingByName.getOrDefault(spec.name().toLowerCase(), new MenuItem());
                item.setName(spec.name());
                item.setDescription(spec.description());
                item.setCategory(spec.category());
                item.setPrice(spec.basePrice());
                item.setHotPrice(spec.hotPrice());
                item.setIcedPrice(spec.icedPrice());
                item.setBlendedPrice(spec.blendedPrice());
                item.setImageUrl(spec.imageUrl());
                item.setIsAvailable(true);
                toSave.add(item);
            }

            menuItemRepository.saveAll(toSave);
            menuItemRepository.saveAll(existingByName.values());
        };
    }

    private void alignServingTypeColumn(JdbcTemplate jdbcTemplate, String tableName) {
        if (!tableExists(jdbcTemplate, tableName)) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN IF NOT EXISTS serving_type VARCHAR(16)");
        jdbcTemplate.execute("UPDATE " + tableName + " SET serving_type = 'NONE' WHERE serving_type IS NULL OR btrim(serving_type) = ''");
        jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN serving_type SET DEFAULT 'NONE'");
        jdbcTemplate.execute("ALTER TABLE " + tableName + " ALTER COLUMN serving_type SET NOT NULL");
    }

    private boolean tableExists(JdbcTemplate jdbcTemplate, String tableName) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?)",
                Boolean.class,
                tableName
        );
        return Boolean.TRUE.equals(exists);
    }

    private List<MenuItemSpec> buildMenuSpecs() {
        return List.of(
                spec("Americano", "Rich espresso diluted with water.", MenuCategory.COFFEE, null, "95", "109", null),
                spec("Cappuccino", "Espresso with steamed milk and foam.", MenuCategory.COFFEE, null, "105", "109", null),
                spec("Latte", "Espresso with creamy steamed milk.", MenuCategory.COFFEE, null, "105", "109", null),
                spec("Caramel Latte", "House caramel latte.", MenuCategory.COFFEE, null, "109", "115", null),
                spec("Spanish Latte", "Sweet condensed milk latte.", MenuCategory.COFFEE, null, "119", "119", null),

                spec("Hazelnut Latte", "Flavored latte series.", MenuCategory.FLAVORED_LATTE, null, null, "129", null),
                spec("Oreo Latte", "Flavored latte with cookies and cream notes.", MenuCategory.FLAVORED_LATTE, null, null, "129", null),
                spec("Mocha Latte", "Chocolate espresso latte.", MenuCategory.FLAVORED_LATTE, null, null, "130", null),

                spec("Matcha", "Classic matcha drink.", MenuCategory.MATCHA_SERIES, null, "105", "109", null),
                spec("Strawberry Matcha", "Strawberry matcha fusion.", MenuCategory.MATCHA_SERIES, null, null, "125", null),
                spec("Blueberry Matcha", "Blueberry matcha fusion.", MenuCategory.MATCHA_SERIES, null, null, "125", null),
                spec("Dirty Matcha (Coffee)", "Matcha with coffee shot.", MenuCategory.MATCHA_SERIES, null, null, "125", null),

                spec("Blueberry Ade", "Iced ade drink.", MenuCategory.BEVERAGES, null, null, "99", null),
                spec("Strawberry Ade", "Iced ade drink.", MenuCategory.BEVERAGES, null, null, "99", null),
                spec("Lychee Ade", "Iced ade drink.", MenuCategory.BEVERAGES, null, null, "99", null),
                spec("Green Apple Ade", "Iced ade drink.", MenuCategory.BEVERAGES, null, null, "99", null),
                spec("Passion Fruit Ade", "Iced ade drink.", MenuCategory.BEVERAGES, null, null, "99", null),
                spec("Chocolate", "Chocolate drink.", MenuCategory.BEVERAGES, null, "105", "109", null),
                spec("Blueberry Milk", "Iced milk series.", MenuCategory.BEVERAGES, null, null, "105", null),
                spec("Strawberry Milk", "Iced milk series.", MenuCategory.BEVERAGES, null, null, "105", null),
                spec("Green Apple Milk", "Iced milk series.", MenuCategory.BEVERAGES, null, null, "105", null),

                spec("Espresso Shot", "Coffee add-on.", MenuCategory.COFFEE_ADD_ON, "60", null, null, null),
                spec("Sugar (Simple Syrup)", "Coffee add-on.", MenuCategory.COFFEE_ADD_ON, "15", null, null, null),
                spec("Hazelnut Syrup", "Coffee add-on.", MenuCategory.COFFEE_ADD_ON, "25", null, null, null),
                spec("Vanilla Syrup", "Coffee add-on.", MenuCategory.COFFEE_ADD_ON, "25", null, null, null)
        );
    }

    private MenuItemSpec spec(
            String name,
            String description,
            MenuCategory category,
            String basePrice,
            String hotPrice,
            String icedPrice,
            String blendedPrice
    ) {
        BigDecimal parsedBasePrice;
        if (basePrice != null) {
            parsedBasePrice = new BigDecimal(basePrice);
        } else if (hotPrice != null) {
            parsedBasePrice = new BigDecimal(hotPrice);
        } else if (icedPrice != null) {
            parsedBasePrice = new BigDecimal(icedPrice);
        } else {
            parsedBasePrice = new BigDecimal(blendedPrice);
        }

        return new MenuItemSpec(
                name,
                description,
                category,
                parsedBasePrice,
                toBigDecimal(hotPrice),
                toBigDecimal(icedPrice),
                toBigDecimal(blendedPrice),
                null
        );
    }

    private BigDecimal toBigDecimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    private record MenuItemSpec(
            String name,
            String description,
            MenuCategory category,
            BigDecimal basePrice,
            BigDecimal hotPrice,
            BigDecimal icedPrice,
            BigDecimal blendedPrice,
            String imageUrl
    ) {
    }
}

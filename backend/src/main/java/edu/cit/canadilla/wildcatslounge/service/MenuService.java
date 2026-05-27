package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.common.MenuCustomizationRules;
import edu.cit.canadilla.wildcatslounge.dto.MenuItemRequest;
import edu.cit.canadilla.wildcatslounge.dto.MenuItemResponse;
import edu.cit.canadilla.wildcatslounge.entity.Ingredient;
import edu.cit.canadilla.wildcatslounge.entity.MenuItem;
import edu.cit.canadilla.wildcatslounge.repository.IngredientRepository;
import edu.cit.canadilla.wildcatslounge.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * Student menu: include all items, but compute availability using both staff toggle + inventory.
     * This allows the UI to gray out unavailable items.
     */
    public List<MenuItemResponse> getMenuForStudents() {
        Map<String, Ingredient> byName = ingredientRepository.findAllByOrderByNameAsc().stream()
                .collect(Collectors.toMap(i -> normalize(i.getName()), i -> i, (a, b) -> a));

        return menuItemRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .peek(MenuCustomizationRules::applyDefaults)
                .map(item -> MenuItemResponse.from(item, inventorySufficient(item, byName)))
                .collect(Collectors.toList());
    }

    public List<MenuItemResponse> getAllMenu() {
        Map<String, Ingredient> byName = ingredientRepository.findAllByOrderByNameAsc().stream()
                .collect(Collectors.toMap(i -> normalize(i.getName()), i -> i, (a, b) -> a));
        return menuItemRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .peek(MenuCustomizationRules::applyDefaults)
                .map(item -> MenuItemResponse.from(item, inventorySufficient(item, byName)))
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuItemResponse create(MenuItemRequest request) {
        MenuItem item = new MenuItem();
        applyRequest(item, request);
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    @Transactional
    public MenuItemResponse update(Long id, MenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        applyRequest(item, request);
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    @Transactional
    public MenuItemResponse toggleAvailability(Long id, boolean available) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        MenuCustomizationRules.applyDefaults(item);

        Map<String, Ingredient> byName = ingredientRepository.findAllByOrderByNameAsc().stream()
                .collect(Collectors.toMap(i -> normalize(i.getName()), i -> i, (a, b) -> a));

        if (available) {
            List<String> missing = missingIngredients(item, byName);
            if (!missing.isEmpty()) {
                throw new RuntimeException(
                        "Cannot mark this item as available. Missing or out-of-stock ingredients: "
                                + String.join(", ", missing));
            }
        }

        item.setIsAvailable(available);
        MenuItem saved = menuItemRepository.save(item);
        return MenuItemResponse.from(saved, inventorySufficient(saved, byName));
    }

    @Transactional
    public void delete(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Menu item not found");
        }
        menuItemRepository.deleteById(id);
    }

    private void applyRequest(MenuItem item, MenuItemRequest request) {
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory().toLowerCase());
        item.setPrice(request.getPrice());
        if (request.getIsAvailable() != null) {
            item.setIsAvailable(request.getIsAvailable());
        }
        item.setImageUrl(request.getImageUrl());
        item.setAllowHot(request.getAllowHot());
        item.setAllowIced(request.getAllowIced());
        item.setAllowBlended(request.getAllowBlended());
        item.setAllowAddons(request.getAllowAddons());
        item.setAllowSugarLevel(request.getAllowSugarLevel());
        MenuCustomizationRules.applyDefaults(item);
    }

    private boolean inventorySufficient(MenuItem item, Map<String, Ingredient> byName) {
        return missingIngredients(item, byName).isEmpty();
    }

    private List<String> missingIngredients(MenuItem item, Map<String, Ingredient> byName) {
        List<String> missing = new ArrayList<>();
        for (String ingName : requiredIngredients(item)) {
            Ingredient ing = byName.get(normalize(ingName));
            if (ing == null) {
                missing.add(ingName);
                continue;
            }
            BigDecimal qty = ing.getQuantityOnHand() != null ? ing.getQuantityOnHand() : BigDecimal.ZERO;
            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
                missing.add(ing.getName());
            }
        }
        return missing;
    }

    /**
     * Very small heuristic-based "recipe" so we can gray out items when key ingredients are out.
     * (No recipe table yet.)
     */
    private List<String> requiredIngredients(MenuItem item) {
        String name = normalize(item.getName());
        String category = normalize(item.getCategory());

        List<String> req = new ArrayList<>();

        boolean isTreat = category.equals("treat") || category.equals("treats");
        boolean isAddon = MenuCustomizationRules.isAddonCategory(category) || name.contains("syrup") || name.contains("shot");

        if (isAddon) {
            if (name.contains("hazelnut")) req.add("Hazelnut Syrup");
            if (name.contains("vanilla")) req.add("Vanilla Syrup");
            if (name.contains("simple") || name.equals("sugar") || name.contains("syrup")) req.add("Simple Syrup");
            if (name.contains("espresso") || name.contains("shot")) req.add("Espresso Beans");
            return req;
        }

        if (!isTreat) {
            // Ice is used for most non-treat drinks that can be iced or are explicitly iced.
            if (name.contains("iced") || Boolean.TRUE.equals(item.getAllowIced())) {
                req.add("Ice");
            }
        }

        if (category.equals("coffee") || name.contains("espresso") || name.contains("americano") || name.contains("macchiato")) {
            req.add("Espresso Beans");
        }

        if (name.contains("matcha")) {
            req.add("Matcha Powder");
            req.add("Oat Milk");
        } else if (name.contains("oat")) {
            req.add("Oat Milk");
        } else if (name.contains("latte") || name.contains("macchiato") || name.contains("milk")) {
            req.add("Fresh Milk");
        }

        if (name.contains("vanilla")) req.add("Vanilla Syrup");
        if (name.contains("hazelnut")) req.add("Hazelnut Syrup");
        if (name.contains("simple")) req.add("Simple Syrup");
        if (name.contains("chocolate")) req.add("Chocolate Sauce");
        if (name.contains("whipped")) req.add("Whipped Cream");

        return req;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ENGLISH);
    }
}

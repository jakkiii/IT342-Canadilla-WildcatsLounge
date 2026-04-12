package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.dto.MenuItemResponse;
import edu.cit.canadilla.wildcatslounge.entity.MenuCategory;
import edu.cit.canadilla.wildcatslounge.entity.MenuItem;
import edu.cit.canadilla.wildcatslounge.entity.ServingType;
import edu.cit.canadilla.wildcatslounge.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public List<MenuItemResponse> getMenuItems(String category) {
        List<MenuItem> items;

        if (category == null || category.isBlank() || category.equalsIgnoreCase("all")) {
            items = menuItemRepository.findByIsAvailableTrueOrderByNameAsc();
        } else {
            MenuCategory parsedCategory = parseCategory(category);
            items = menuItemRepository.findByCategoryAndIsAvailableTrueOrderByNameAsc(parsedCategory);
        }

        return items.stream().map(this::toResponse).toList();
    }

    private MenuCategory parseCategory(String rawCategory) {
        String normalized = rawCategory.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        if ("NON_COFFEE".equals(normalized)) {
            return MenuCategory.BEVERAGES;
        }
        try {
            return MenuCategory.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid category: " + rawCategory);
        }
    }

    private MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getCategory(),
                item.getPrice(),
                item.getHotPrice(),
                item.getIcedPrice(),
                item.getBlendedPrice(),
                getServingTypes(item),
                item.getIsAvailable(),
                item.getImageUrl()
        );
    }

    private List<ServingType> getServingTypes(MenuItem item) {
        List<ServingType> servingTypes = new ArrayList<>();

        if (item.getHotPrice() != null) {
            servingTypes.add(ServingType.HOT);
        }
        if (item.getIcedPrice() != null) {
            servingTypes.add(ServingType.ICED);
        }
        if (item.getBlendedPrice() != null) {
            servingTypes.add(ServingType.BLENDED);
        }

        if (servingTypes.isEmpty()) {
            servingTypes.add(ServingType.NONE);
        }

        return servingTypes;
    }
}

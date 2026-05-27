package edu.cit.canadilla.wildcatslounge.service;

import edu.cit.canadilla.wildcatslounge.common.MenuCustomizationRules;
import edu.cit.canadilla.wildcatslounge.common.ServingType;
import edu.cit.canadilla.wildcatslounge.dto.CartItemRequest;
import edu.cit.canadilla.wildcatslounge.dto.CartResponse;
import edu.cit.canadilla.wildcatslounge.entity.Cart;
import edu.cit.canadilla.wildcatslounge.entity.CartItem;
import edu.cit.canadilla.wildcatslounge.entity.MenuItem;
import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.repository.CartItemRepository;
import edu.cit.canadilla.wildcatslounge.repository.CartRepository;
import edu.cit.canadilla.wildcatslounge.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;

    @Transactional(readOnly = true)
    public CartResponse getCart(User user) {
        Cart cart = getOrCreateCart(user);
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse addItem(User user, CartItemRequest request) {
        Cart cart = getOrCreateCart(user);
        MenuItem menuItem = requireMenuItem(request.getMenuItemId());
        if (MenuCustomizationRules.isAddonCategory(menuItem.getCategory())) {
            throw new RuntimeException("Add-ons must be attached to a coffee order.");
        }

        ServingType servingType = request.getServingType() != null ? request.getServingType() : ServingType.NONE;
        Integer sugarLevelPercent = request.getSugarLevelPercent();
        String notes = sanitizeNotes(request.getCustomizationNotes());
        List<MenuItem> selectedAddons = resolveAddons(menuItem, request.getAddonIds());
        validateBaseCustomization(menuItem, servingType, sugarLevelPercent);

        CartItem existing = findMatchingRootItem(cart, menuItem, notes, servingType, sugarLevelPercent, selectedAddons);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            syncChildQuantities(existing, cart);
        } else {
            CartItem parent = new CartItem();
            parent.setCart(cart);
            parent.setMenuItem(menuItem);
            parent.setQuantity(request.getQuantity());
            parent.setCustomizationNotes(notes);
            parent.setServingType(servingType);
            parent.setSugarLevelPercent(sugarLevelPercent);
            cart.getItems().add(parent);

            for (MenuItem addon : selectedAddons) {
                CartItem child = new CartItem();
                child.setCart(cart);
                child.setMenuItem(addon);
                child.setParentItem(parent);
                child.setQuantity(request.getQuantity());
                child.setServingType(ServingType.NONE);
                cart.getItems().add(child);
            }
        }

        return CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItemQuantity(User user, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCart(user);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        CartItem target = item.getParentItem() != null ? item.getParentItem() : item;

        if (quantity <= 0) {
            removeRootItem(cart, target);
        } else {
            target.setQuantity(quantity);
            syncChildQuantities(target, cart);
        }
        return CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        removeRootItem(cart, item.getParentItem() != null ? item.getParentItem() : item);
        return CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public Cart getOrCreateCartEntity(User user) {
        return getOrCreateCart(user);
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    private boolean notesEqual(String a, String b) {
        String na = a == null ? "" : a.trim();
        String nb = b == null ? "" : b.trim();
        return na.equals(nb);
    }

    private boolean servingTypesEqual(ServingType a, ServingType b) {
        ServingType sa = a != null ? a : ServingType.NONE;
        ServingType sb = b != null ? b : ServingType.NONE;
        return sa == sb;
    }

    private MenuItem requireMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        MenuCustomizationRules.applyDefaults(menuItem);
        if (!Boolean.TRUE.equals(menuItem.getIsAvailable())) {
            throw new RuntimeException("Item is out of stock");
        }
        return menuItem;
    }

    private void validateBaseCustomization(MenuItem menuItem, ServingType servingType, Integer sugarLevelPercent) {
        if (MenuCustomizationRules.requiresServingType(menuItem) && servingType == ServingType.NONE) {
            throw new RuntimeException("Serving type is required for " + menuItem.getName());
        }
        if (!MenuCustomizationRules.allowsServingType(menuItem, servingType)) {
            throw new RuntimeException("Selected serving type is not available for " + menuItem.getName());
        }

        if (MenuCustomizationRules.supportsSugarLevel(menuItem)) {
            if (!MenuCustomizationRules.isValidSugarLevel(sugarLevelPercent)) {
                throw new RuntimeException("Sugar level must be 0%, 25%, 50%, 75%, or 100%.");
            }
        } else if (sugarLevelPercent != null) {
            throw new RuntimeException("Sugar level is not available for " + menuItem.getName());
        }
    }

    private List<MenuItem> resolveAddons(MenuItem menuItem, List<Long> addonIds) {
        Set<Long> uniqueAddonIds = addonIds == null ? Set.of() : new LinkedHashSet<>(addonIds);
        if (uniqueAddonIds.isEmpty()) {
            return List.of();
        }
        if (!MenuCustomizationRules.supportsAddons(menuItem)) {
            throw new RuntimeException("Add-ons are not available for " + menuItem.getName());
        }

        List<MenuItem> addons = new ArrayList<>();
        for (Long addonId : uniqueAddonIds) {
            MenuItem addon = requireMenuItem(addonId);
            if (!MenuCustomizationRules.isAddonCategory(addon.getCategory())) {
                throw new RuntimeException(addon.getName() + " is not a valid add-on.");
            }
            addons.add(addon);
        }
        return addons;
    }

    private CartItem findMatchingRootItem(
            Cart cart,
            MenuItem menuItem,
            String notes,
            ServingType servingType,
            Integer sugarLevelPercent,
            List<MenuItem> selectedAddons) {
        List<Long> requestedAddonIds = selectedAddons.stream()
                .map(MenuItem::getId)
                .sorted()
                .toList();

        return cart.getItems().stream()
                .filter(i -> i.getParentItem() == null)
                .filter(i -> i.getMenuItem().getId().equals(menuItem.getId()))
                .filter(i -> notesEqual(i.getCustomizationNotes(), notes))
                .filter(i -> servingTypesEqual(i.getServingType(), servingType))
                .filter(i -> Objects.equals(i.getSugarLevelPercent(), sugarLevelPercent))
                .filter(i -> matchingAddonIds(cart, i).equals(requestedAddonIds))
                .findFirst()
                .orElse(null);
    }

    private List<Long> matchingAddonIds(Cart cart, CartItem parent) {
        return cart.getItems().stream()
                .filter(child -> child.getParentItem() != null && child.getParentItem().getId().equals(parent.getId()))
                .map(child -> child.getMenuItem().getId())
                .sorted()
                .collect(Collectors.toList());
    }

    private void syncChildQuantities(CartItem parent, Cart cart) {
        cart.getItems().stream()
                .filter(child -> child.getParentItem() != null && child.getParentItem().getId().equals(parent.getId()))
                .forEach(child -> child.setQuantity(parent.getQuantity()));
    }

    private void removeRootItem(Cart cart, CartItem root) {
        List<Long> childIds = cart.getItems().stream()
                .filter(child -> child.getParentItem() != null && child.getParentItem().getId().equals(root.getId()))
                .map(CartItem::getId)
                .collect(Collectors.toList());
        cart.getItems().removeIf(i -> i.getId().equals(root.getId()) || childIds.contains(i.getId()));
        if (root.getId() != null) {
            cartItemRepository.deleteByParentItem_Id(root.getId());
        }
    }

    private String sanitizeNotes(String notes) {
        if (notes == null) {
            return null;
        }
        String trimmed = notes.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

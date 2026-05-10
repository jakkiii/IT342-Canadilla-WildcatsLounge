package edu.cit.canadilla.wildcatslounge.feature.cart.service;

import edu.cit.canadilla.wildcatslounge.feature.cart.dto.AddCartItemRequest;
import edu.cit.canadilla.wildcatslounge.feature.cart.dto.CartItemResponse;
import edu.cit.canadilla.wildcatslounge.feature.cart.dto.CartResponse;
import edu.cit.canadilla.wildcatslounge.feature.cart.dto.UpdateCartItemRequest;
import edu.cit.canadilla.wildcatslounge.feature.cart.entity.Cart;
import edu.cit.canadilla.wildcatslounge.feature.cart.entity.CartItem;
import edu.cit.canadilla.wildcatslounge.feature.menu.entity.MenuItem;
import edu.cit.canadilla.wildcatslounge.common.ServingType;
import edu.cit.canadilla.wildcatslounge.feature.auth.entity.User;
import edu.cit.canadilla.wildcatslounge.feature.cart.repository.CartItemRepository;
import edu.cit.canadilla.wildcatslounge.feature.cart.repository.CartRepository;
import edu.cit.canadilla.wildcatslounge.feature.menu.repository.MenuItemRepository;
import edu.cit.canadilla.wildcatslounge.feature.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(Long userId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        if (!Boolean.TRUE.equals(menuItem.getIsAvailable())) {
            throw new RuntimeException("Menu item is not available");
        }

        ServingType servingType = resolveServingType(menuItem, request.getServingType());

        CartItem cartItem = cartItemRepository.findByCartIdAndMenuItemIdAndServingType(cart.getId(), menuItem.getId(), servingType)
                .orElseGet(() -> {
                    CartItem item = new CartItem();
                    item.setCart(cart);
                    item.setMenuItem(menuItem);
                    item.setQuantity(0);
                item.setServingType(servingType);
                    return item;
                });

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItem.setCustomizationNotes(trimToNull(request.getCustomizationNotes()));
        cartItem.setServingType(servingType);
        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (request.getServingType() != null) {
            ServingType resolvedServingType = resolveServingType(cartItem.getMenuItem(), request.getServingType());
            cartItem.setServingType(resolvedServingType);
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem.setCustomizationNotes(trimToNull(request.getCustomizationNotes()));
        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItemRepository.delete(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return buildCartResponse(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public List<CartItem> getCartItems(Cart cart) {
        return cartItemRepository.findByCartIdOrderByIdAsc(cart.getId());
    }

    @Transactional
    protected CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = getCartItems(cart).stream()
                .map(this::toCartItemResponse)
                .toList();

        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int itemCount = itemResponses.stream()
                .map(CartItemResponse::getQuantity)
                .reduce(0, Integer::sum);

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                itemCount,
                subtotal,
                cart.getUpdatedAt(),
                itemResponses
        );
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        BigDecimal unitPrice = resolveUnitPrice(item.getMenuItem(), item.getServingType());
        BigDecimal lineTotal = unitPrice
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getId(),
                item.getMenuItem().getId(),
                item.getMenuItem().getName(),
                item.getMenuItem().getCategory(),
                unitPrice,
                item.getQuantity(),
                item.getCustomizationNotes(),
                item.getServingType(),
                item.getMenuItem().getImageUrl(),
                lineTotal
        );
    }

    public BigDecimal resolveUnitPrice(MenuItem menuItem, ServingType servingType) {
        if (servingType == null || servingType == ServingType.NONE) {
            return menuItem.getPrice();
        }

        return switch (servingType) {
            case HOT -> menuItem.getHotPrice();
            case ICED -> menuItem.getIcedPrice();
            case BLENDED -> menuItem.getBlendedPrice();
            case NONE -> menuItem.getPrice();
        };
    }

    private ServingType resolveServingType(MenuItem menuItem, ServingType requestedServingType) {
        boolean hasHot = menuItem.getHotPrice() != null;
        boolean hasIced = menuItem.getIcedPrice() != null;
        boolean hasBlended = menuItem.getBlendedPrice() != null;

        if (!hasHot && !hasIced && !hasBlended) {
            return ServingType.NONE;
        }

        if (requestedServingType == null || requestedServingType == ServingType.NONE) {
            throw new RuntimeException("Serving type is required for " + menuItem.getName());
        }

        boolean available = switch (requestedServingType) {
            case HOT -> hasHot;
            case ICED -> hasIced;
            case BLENDED -> hasBlended;
            case NONE -> false;
        };

        if (!available) {
            throw new RuntimeException("Selected serving type is not available for " + menuItem.getName());
        }

        return requestedServingType;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

package edu.cit.canadilla.wildcatslounge.controller;

import edu.cit.canadilla.wildcatslounge.dto.ApiResponse;
import edu.cit.canadilla.wildcatslounge.dto.CartItemRequest;
import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.service.AuthHelperService;
import edu.cit.canadilla.wildcatslounge.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    private final AuthHelperService authHelperService;

    @GetMapping
    public ResponseEntity<ApiResponse> getCart(HttpServletRequest request) {
        try {
            User user = authHelperService.requireUser(request);
            return ResponseEntity.ok(ApiResponse.success(cartService.getCart(user)));
        } catch (RuntimeException e) {
            return unauthorized(e);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addItem(
            HttpServletRequest request,
            @Valid @RequestBody CartItemRequest body) {
        try {
            User user = authHelperService.requireUser(request);
            return ResponseEntity.ok(ApiResponse.success(cartService.addItem(user, body)));
        } catch (RuntimeException e) {
            return badRequest(e);
        }
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse> updateQuantity(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        try {
            User user = authHelperService.requireUser(request);
            int qty = body.getOrDefault("quantity", 1);
            return ResponseEntity.ok(ApiResponse.success(cartService.updateItemQuantity(user, id, qty)));
        } catch (RuntimeException e) {
            return badRequest(e);
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse> removeItem(HttpServletRequest request, @PathVariable Long id) {
        try {
            User user = authHelperService.requireUser(request);
            return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(user, id)));
        } catch (RuntimeException e) {
            return badRequest(e);
        }
    }

    private ResponseEntity<ApiResponse> unauthorized(RuntimeException e) {
        if ("Unauthorized".equals(e.getMessage())) {
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }

    private ResponseEntity<ApiResponse> badRequest(RuntimeException e) {
        if ("Unauthorized".equals(e.getMessage())) {
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }
}

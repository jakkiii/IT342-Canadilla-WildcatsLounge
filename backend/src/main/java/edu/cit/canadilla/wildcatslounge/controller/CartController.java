package edu.cit.canadilla.wildcatslounge.controller;

import edu.cit.canadilla.wildcatslounge.dto.AddCartItemRequest;
import edu.cit.canadilla.wildcatslounge.dto.ApiResponse;
import edu.cit.canadilla.wildcatslounge.dto.UpdateCartItemRequest;
import edu.cit.canadilla.wildcatslounge.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getCart(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userId)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<ApiResponse> addItem(
            @PathVariable Long userId,
            @Valid @RequestBody AddCartItemRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(ApiResponse.error(errors));
        }

        try {
            return ResponseEntity.ok(ApiResponse.success(cartService.addItem(userId, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PutMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponse> updateItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(ApiResponse.error(errors));
        }

        try {
            return ResponseEntity.ok(ApiResponse.success(cartService.updateItem(userId, cartItemId, request)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponse> removeItem(@PathVariable Long userId, @PathVariable Long cartItemId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(userId, cartItemId)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }
}

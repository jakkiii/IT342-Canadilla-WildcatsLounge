package edu.cit.canadilla.wildcatslounge.controller;

import edu.cit.canadilla.wildcatslounge.dto.ApiResponse;
import edu.cit.canadilla.wildcatslounge.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<ApiResponse> checkout(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(orderService.checkout(userId)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getOrders(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(userId)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }
}

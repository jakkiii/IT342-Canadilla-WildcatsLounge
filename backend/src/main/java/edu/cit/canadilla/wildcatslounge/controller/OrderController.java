package edu.cit.canadilla.wildcatslounge.controller;

import edu.cit.canadilla.wildcatslounge.dto.ApiResponse;
import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.service.AuthHelperService;
import edu.cit.canadilla.wildcatslounge.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final AuthHelperService authHelperService;

    @PostMapping
    public ResponseEntity<ApiResponse> placeOrder(HttpServletRequest request) {
        try {
            User user = authHelperService.requireUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(orderService.placeOrder(user)));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse> myOrders(HttpServletRequest request) {
        try {
            User user = authHelperService.requireUser(request);
            return ResponseEntity.ok(ApiResponse.success(orderService.getMyOrders(user)));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getOrder(HttpServletRequest request, @PathVariable Long id) {
        try {
            User user = authHelperService.requireUser(request);
            boolean staff = "staff".equalsIgnoreCase(user.getRole());
            return ResponseEntity.ok(ApiResponse.success(orderService.getOrder(id, user, staff)));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    private ResponseEntity<ApiResponse> handleError(RuntimeException e) {
        String msg = e.getMessage();
        if ("Unauthorized".equals(msg)) {
            return ResponseEntity.status(401).body(ApiResponse.error(msg));
        }
        if ("Forbidden".equals(msg)) {
            return ResponseEntity.status(403).body(ApiResponse.error(msg));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(msg));
    }
}

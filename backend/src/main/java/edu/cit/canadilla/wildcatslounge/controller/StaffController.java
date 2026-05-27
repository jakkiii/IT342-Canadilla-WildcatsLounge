package edu.cit.canadilla.wildcatslounge.controller;

import edu.cit.canadilla.wildcatslounge.dto.*;
import edu.cit.canadilla.wildcatslounge.entity.User;
import edu.cit.canadilla.wildcatslounge.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffController {

    private final AuthHelperService authHelperService;
    private final OrderService orderService;
    private final MenuService menuService;
    private final EventService eventService;
    private final LoungeStatusService loungeStatusService;
    private final InventoryService inventoryService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> dashboard(HttpServletRequest request) {
        try {
            authHelperService.requireStaff(request);
            Map<String, Object> stats = new HashMap<>();
            stats.put("pendingOrders", orderService.countPendingOrders());
            stats.put("loungeStatus", loungeStatusService.getCurrentStatus());
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse> activeOrders(HttpServletRequest request) {
        try {
            authHelperService.requireStaff(request);
            return ResponseEntity.ok(ApiResponse.success(orderService.getActiveOrdersForStaff()));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest body,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationError(bindingResult);
        }
        try {
            authHelperService.requireStaff(request);
            return ResponseEntity.ok(ApiResponse.success(orderService.updateStatus(id, body.getStatus())));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @GetMapping("/menu")
    public ResponseEntity<ApiResponse> allMenu(HttpServletRequest request) {
        try {
            authHelperService.requireStaff(request);
            return ResponseEntity.ok(ApiResponse.success(menuService.getAllMenu()));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PostMapping("/menu")
    public ResponseEntity<ApiResponse> createMenu(
            HttpServletRequest request,
            @Valid @RequestBody MenuItemRequest body,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationError(bindingResult);
        }
        try {
            authHelperService.requireStaff(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(menuService.create(body)));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PutMapping("/menu/{id}")
    public ResponseEntity<ApiResponse> updateMenu(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest body,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationError(bindingResult);
        }
        try {
            authHelperService.requireStaff(request);
            return ResponseEntity.ok(ApiResponse.success(menuService.update(id, body)));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PatchMapping("/menu/{id}/availability")
    public ResponseEntity<ApiResponse> toggleMenu(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        try {
            authHelperService.requireStaff(request);
            boolean available = body.getOrDefault("isAvailable", true);
            return ResponseEntity.ok(ApiResponse.success(menuService.toggleAvailability(id, available)));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @DeleteMapping("/menu/{id}")
    public ResponseEntity<ApiResponse> deleteMenu(HttpServletRequest request, @PathVariable Long id) {
        try {
            authHelperService.requireStaff(request);
            menuService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Menu item deleted"));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse> allEvents(HttpServletRequest request) {
        try {
            authHelperService.requireStaff(request);
            return ResponseEntity.ok(ApiResponse.success(eventService.getAllEvents()));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse> createEvent(
            HttpServletRequest request,
            @Valid @RequestBody EventRequest body,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationError(bindingResult);
        }
        try {
            User staff = authHelperService.requireStaff(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(eventService.create(body, staff)));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<ApiResponse> updateEvent(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody EventRequest body,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationError(bindingResult);
        }
        try {
            authHelperService.requireStaff(request);
            return ResponseEntity.ok(ApiResponse.success(eventService.update(id, body)));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<ApiResponse> deleteEvent(HttpServletRequest request, @PathVariable Long id) {
        try {
            authHelperService.requireStaff(request);
            eventService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Event deleted"));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @GetMapping("/inventory/ingredients")
    public ResponseEntity<ApiResponse> listIngredients(HttpServletRequest request) {
        try {
            authHelperService.requireStaff(request);
            return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllIngredients()));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PostMapping("/inventory/ingredients/{id}/restock")
    public ResponseEntity<ApiResponse> restockIngredient(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody RestockRequest body,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationError(bindingResult);
        }
        try {
            authHelperService.requireStaff(request);
            return ResponseEntity.ok(ApiResponse.success(inventoryService.restock(id, body.getAmount())));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    @PutMapping("/lounge/status")
    public ResponseEntity<ApiResponse> updateLounge(
            HttpServletRequest request,
            @Valid @RequestBody LoungeStatusRequest body,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationError(bindingResult);
        }
        try {
            User staff = authHelperService.requireStaff(request);
            return ResponseEntity.ok(ApiResponse.success(loungeStatusService.updateStatus(body, staff)));
        } catch (RuntimeException e) {
            return handleError(e);
        }
    }

    private ResponseEntity<ApiResponse> validationError(BindingResult bindingResult) {
        String errors = bindingResult.getAllErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.error(errors));
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

package edu.cit.canadilla.wildcatslounge.controller;



import edu.cit.canadilla.wildcatslounge.config.DataInitializer;

import edu.cit.canadilla.wildcatslounge.dto.ApiResponse;

import edu.cit.canadilla.wildcatslounge.dto.AuthResponse;

import edu.cit.canadilla.wildcatslounge.dto.CartItemRequest;

import edu.cit.canadilla.wildcatslounge.dto.LoginRequest;
import edu.cit.canadilla.wildcatslounge.dto.LoginVerifyRequest;

import edu.cit.canadilla.wildcatslounge.dto.LoungeStatusRequest;

import edu.cit.canadilla.wildcatslounge.dto.OrderStatusRequest;

import edu.cit.canadilla.wildcatslounge.dto.RestockRequest;

import edu.cit.canadilla.wildcatslounge.dto.RegisterRequest;
import edu.cit.canadilla.wildcatslounge.dto.RegisterVerifyRequest;

import edu.cit.canadilla.wildcatslounge.entity.User;

import edu.cit.canadilla.wildcatslounge.service.AuthHelperService;

import edu.cit.canadilla.wildcatslounge.service.CartService;

import edu.cit.canadilla.wildcatslounge.service.EventService;
import edu.cit.canadilla.wildcatslounge.service.EmailVerificationService;

import edu.cit.canadilla.wildcatslounge.service.LoungeStatusService;

import edu.cit.canadilla.wildcatslounge.service.MenuService;

import edu.cit.canadilla.wildcatslounge.service.InventoryService;

import edu.cit.canadilla.wildcatslounge.service.OrderService;

import edu.cit.canadilla.wildcatslounge.service.UserService;

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

@RequestMapping("/api/auth")

@RequiredArgsConstructor

@CrossOrigin(origins = "*")

public class AuthController {



    private final UserService userService;

    private final EmailVerificationService emailVerificationService;

    private final DataInitializer dataInitializer;

    private final MenuService menuService;

    private final EventService eventService;

    private final LoungeStatusService loungeStatusService;

    private final CartService cartService;

    private final OrderService orderService;

    private final AuthHelperService authHelperService;

    private final InventoryService inventoryService;



    // ── Health & auth ─────────────────────────────────────────────────────



    @GetMapping("/health")

    public ResponseEntity<ApiResponse> health() {

        Map<String, Object> info = new HashMap<>();

        info.put("message", "Wildcats Lounge API is running");

        info.put("staffApi", true);

        info.put("inventoryApi", true);

        return ResponseEntity.ok(ApiResponse.success(info));

    }



    @GetMapping("/sync-admin")

    @PostMapping("/sync-admin")

    public ResponseEntity<ApiResponse> syncAdmin() {

        dataInitializer.seedAdminUser();

        return ResponseEntity.ok(ApiResponse.success(

                "Staff admin synced. Login with staff.administrator@gmail.com / Welcome1!"

        ));

    }



    @PostMapping("/register")

    public ResponseEntity<ApiResponse> register(

            @Valid @RequestBody RegisterRequest request,

            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return validationError(bindingResult);

        }

        try {

            AuthResponse authResponse = userService.registerUser(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authResponse));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));

        }

    }

    @PostMapping("/register/send-code")

    public ResponseEntity<ApiResponse> sendRegisterCode(

            @Valid @RequestBody RegisterRequest request,

            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return validationError(bindingResult);

        }

        try {

            userService.assertCanRegister(request);
            emailVerificationService.sendRegisterCode(request.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Verification code sent to your email."));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));

        }

    }

    @PostMapping("/register/verify")

    public ResponseEntity<ApiResponse> verifyRegister(

            @Valid @RequestBody RegisterVerifyRequest request,

            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return validationError(bindingResult);

        }

        try {

            RegisterRequest registerRequest = request.toRegisterRequest();
            userService.assertCanRegister(registerRequest);
            emailVerificationService.verifyRegisterCode(registerRequest.getEmail(), request.getVerificationCode());
            AuthResponse authResponse = userService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authResponse));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));

        }

    }



    @PostMapping("/login")

    public ResponseEntity<ApiResponse> login(

            @Valid @RequestBody LoginRequest request,

            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return validationError(bindingResult);

        }

        try {

            AuthResponse authResponse = userService.loginUser(request);

            return ResponseEntity.ok(ApiResponse.success(authResponse));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));

        }

    }

    @PostMapping("/login/send-code")

    public ResponseEntity<ApiResponse> sendLoginCode(

            @Valid @RequestBody LoginRequest request,

            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return validationError(bindingResult);

        }

        try {

            User user = userService.authenticateUser(request);
            emailVerificationService.sendLoginCode(user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Verification code sent to your email."));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));

        }

    }

    @PostMapping("/login/verify")

    public ResponseEntity<ApiResponse> verifyLogin(

            @Valid @RequestBody LoginVerifyRequest request,

            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return validationError(bindingResult);

        }

        try {

            User user = userService.authenticateUser(request.toLoginRequest());
            emailVerificationService.verifyLoginCode(user.getEmail(), request.getVerificationCode());
            return ResponseEntity.ok(ApiResponse.success(userService.buildAuthResponse(user)));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));

        }

    }



    // ── Public student reads (no login required) ──────────────────────────



    @GetMapping("/menu")

    public ResponseEntity<ApiResponse> getMenu() {

        return ResponseEntity.ok(ApiResponse.success(menuService.getMenuForStudents()));

    }



    @GetMapping("/events")

    public ResponseEntity<ApiResponse> getEvents() {

        return ResponseEntity.ok(ApiResponse.success(eventService.getUpcomingEvents()));

    }



    @GetMapping("/events/today")

    public ResponseEntity<ApiResponse> getTodayEvents() {

        return ResponseEntity.ok(ApiResponse.success(eventService.getTodayEvents()));

    }



    @GetMapping("/lounge/status")

    public ResponseEntity<ApiResponse> getLoungeStatus() {

        return ResponseEntity.ok(ApiResponse.success(loungeStatusService.getCurrentStatus()));

    }



    // ── Student cart (login required) ─────────────────────────────────────



    @GetMapping("/cart")

    public ResponseEntity<ApiResponse> getCart(HttpServletRequest request) {

        try {

            User user = authHelperService.requireUser(request);

            return ResponseEntity.ok(ApiResponse.success(cartService.getCart(user)));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    @PostMapping("/cart/items")

    public ResponseEntity<ApiResponse> addCartItem(

            HttpServletRequest request,

            @Valid @RequestBody CartItemRequest body) {

        try {

            User user = authHelperService.requireUser(request);

            return ResponseEntity.ok(ApiResponse.success(cartService.addItem(user, body)));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    @PutMapping("/cart/items/{id}")

    public ResponseEntity<ApiResponse> updateCartItem(

            HttpServletRequest request,

            @PathVariable Long id,

            @RequestBody Map<String, Integer> body) {

        try {

            User user = authHelperService.requireUser(request);

            int qty = body.getOrDefault("quantity", 1);

            return ResponseEntity.ok(ApiResponse.success(cartService.updateItemQuantity(user, id, qty)));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    @DeleteMapping("/cart/items/{id}")

    public ResponseEntity<ApiResponse> removeCartItem(HttpServletRequest request, @PathVariable Long id) {

        try {

            User user = authHelperService.requireUser(request);

            return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(user, id)));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    // ── Student orders (login required) ───────────────────────────────────



    @PostMapping("/orders")

    public ResponseEntity<ApiResponse> placeOrder(HttpServletRequest request) {

        try {

            User user = authHelperService.requireUser(request);

            return ResponseEntity.status(HttpStatus.CREATED)

                    .body(ApiResponse.success(orderService.placeOrder(user)));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    @GetMapping("/orders/my")

    public ResponseEntity<ApiResponse> myOrders(HttpServletRequest request) {

        try {

            User user = authHelperService.requireUser(request);

            return ResponseEntity.ok(ApiResponse.success(orderService.getMyOrders(user)));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    @GetMapping("/orders/{id}")

    public ResponseEntity<ApiResponse> getOrder(HttpServletRequest request, @PathVariable Long id) {

        try {

            User user = authHelperService.requireUser(request);

            boolean staff = "staff".equalsIgnoreCase(user.getRole());

            return ResponseEntity.ok(ApiResponse.success(orderService.getOrder(id, user, staff)));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    // ── Staff (under /api/auth/staff — same handlers as /api/staff) ────────



    @GetMapping("/staff/dashboard")

    public ResponseEntity<ApiResponse> staffDashboard(HttpServletRequest request) {

        try {

            authHelperService.requireStaff(request);

            Map<String, Object> stats = new HashMap<>();

            stats.put("pendingOrders", orderService.countPendingOrders());

            stats.put("loungeStatus", loungeStatusService.getCurrentStatus());

            return ResponseEntity.ok(ApiResponse.success(stats));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }

    @GetMapping("/staff/orders/analytics")

    public ResponseEntity<ApiResponse> staffOrderAnalytics(HttpServletRequest request) {

        try {

            authHelperService.requireStaff(request);

            return ResponseEntity.ok(ApiResponse.success(orderService.getStaffOrderAnalytics()));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    @GetMapping("/staff/orders")

    public ResponseEntity<ApiResponse> staffOrders(HttpServletRequest request) {

        try {

            authHelperService.requireStaff(request);

            return ResponseEntity.ok(ApiResponse.success(orderService.getActiveOrdersForStaff()));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    @PutMapping("/staff/orders/{id}/status")

    public ResponseEntity<ApiResponse> staffUpdateOrderStatus(

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

            return handleStudentError(e);

        }

    }



    @PutMapping("/staff/lounge/status")

    public ResponseEntity<ApiResponse> staffUpdateLoungeStatus(

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

            return handleStudentError(e);

        }

    }



    @GetMapping("/staff/inventory/ingredients")

    public ResponseEntity<ApiResponse> staffListIngredients(HttpServletRequest request) {

        try {

            authHelperService.requireStaff(request);

            return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllIngredients()));

        } catch (RuntimeException e) {

            return handleStudentError(e);

        }

    }



    @PostMapping("/staff/inventory/ingredients/{id}/restock")

    public ResponseEntity<ApiResponse> staffRestockIngredient(

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

            return handleStudentError(e);

        }

    }



    private ResponseEntity<ApiResponse> validationError(BindingResult bindingResult) {

        String errors = bindingResult.getAllErrors().stream()

                .map(error -> error.getDefaultMessage())

                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(ApiResponse.error(errors));

    }



    private ResponseEntity<ApiResponse> handleStudentError(RuntimeException e) {

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



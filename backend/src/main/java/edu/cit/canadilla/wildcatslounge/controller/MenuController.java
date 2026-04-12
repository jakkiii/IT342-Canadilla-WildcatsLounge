package edu.cit.canadilla.wildcatslounge.controller;

import edu.cit.canadilla.wildcatslounge.dto.ApiResponse;
import edu.cit.canadilla.wildcatslounge.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse> getMenuItems(@RequestParam(required = false) String category) {
        try {
            return ResponseEntity.ok(ApiResponse.success(menuService.getMenuItems(category)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }
}

package edu.cit.canadilla.wildcatslounge.controller;

import edu.cit.canadilla.wildcatslounge.dto.ApiResponse;
import edu.cit.canadilla.wildcatslounge.service.LoungeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lounge")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LoungeController {

    private final LoungeStatusService loungeStatusService;

    @GetMapping("/status")
    public ResponseEntity<ApiResponse> getStatus() {
        return ResponseEntity.ok(ApiResponse.success(loungeStatusService.getCurrentStatus()));
    }
}

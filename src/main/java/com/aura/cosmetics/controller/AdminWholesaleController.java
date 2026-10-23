package com.aura.cosmetics.controller;

import com.aura.cosmetics.dto.WholesaleRequestDTO;
import com.aura.cosmetics.enums.WholesaleStatus;
import com.aura.cosmetics.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/wholesale-requests")
public class AdminWholesaleController {

    private final AuthService authService;

    public AdminWholesaleController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<WholesaleRequestDTO>> getRequests(
            @RequestParam(required = false) WholesaleStatus status) {
        return ResponseEntity.ok(authService.getWholesaleRequests(status));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<WholesaleRequestDTO> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(authService.approveWholesale(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<WholesaleRequestDTO> rejectRequest(@PathVariable Long id) {
        return ResponseEntity.ok(authService.rejectWholesale(id));
    }
}

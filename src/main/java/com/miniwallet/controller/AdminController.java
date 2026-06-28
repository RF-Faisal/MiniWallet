package com.miniwallet.controller;

import com.miniwallet.dto.response.*;
import com.miniwallet.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(adminService.listUsers(page, size, role));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserWalletResponse> getUserWithWallet(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserWithWallet(id));
    }

    @GetMapping("/transactions")
    public ResponseEntity<PageResponse<TransactionResponse>> listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {
        return ResponseEntity.ok(adminService.listAllTransactions(
                page, size, userId, type, status, fromDate, toDate));
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}
package com.miniwallet.controller;

import com.miniwallet.dto.response.PageResponse;
import com.miniwallet.dto.response.TransactionResponse;
import com.miniwallet.entity.User;
import com.miniwallet.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/me")
    public ResponseEntity<PageResponse<TransactionResponse>> getMyTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {

        PageResponse<TransactionResponse> response = transactionService.getOwnTransactions(
                user.getId(), page, size, type, fromDate, toDate);
        return ResponseEntity.ok(response);
    }
}
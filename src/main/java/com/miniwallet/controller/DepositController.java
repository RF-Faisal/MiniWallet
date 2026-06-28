package com.miniwallet.controller;

import com.miniwallet.dto.request.DepositRequest;
import com.miniwallet.dto.response.TransactionResponse;
import com.miniwallet.entity.User;
import com.miniwallet.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deposits")
public class DepositController {

    private final TransferService transferService;

    public DepositController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> deposit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DepositRequest request) {
        TransactionResponse response = transferService.deposit(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
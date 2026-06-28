package com.miniwallet.controller;

import com.miniwallet.dto.request.TransferRequest;
import com.miniwallet.dto.response.TransactionResponse;
import com.miniwallet.entity.User;
import com.miniwallet.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transferService.transfer(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
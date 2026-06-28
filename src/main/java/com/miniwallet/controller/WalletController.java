package com.miniwallet.controller;

import com.miniwallet.dto.response.WalletResponse;
import com.miniwallet.entity.User;
import com.miniwallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getMyWallet(@AuthenticationPrincipal User user) {
        WalletResponse response = walletService.getWalletByUserId(user.getId());
        return ResponseEntity.ok(response);
    }
}
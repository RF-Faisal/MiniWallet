package com.miniwallet.service;

import com.miniwallet.dto.response.WalletResponse;
import com.miniwallet.entity.Wallet;
import com.miniwallet.exception.ResourceNotFoundException;
import com.miniwallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public WalletResponse getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));
        return toWalletResponse(wallet);
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        return new WalletResponse(wallet.getId(), wallet.getBalance(), wallet.getCurrency(),
                wallet.getCreatedAt(), wallet.getUpdatedAt());
    }
}
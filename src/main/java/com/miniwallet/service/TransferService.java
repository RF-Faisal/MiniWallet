package com.miniwallet.service;

import com.miniwallet.dto.request.DepositRequest;
import com.miniwallet.dto.request.TransferRequest;
import com.miniwallet.dto.response.TransactionResponse;
import com.miniwallet.entity.*;
import com.miniwallet.exception.BusinessRuleException;
import com.miniwallet.exception.InsufficientBalanceException;
import com.miniwallet.exception.ResourceNotFoundException;
import com.miniwallet.repository.TransactionRepository;
import com.miniwallet.repository.UserRepository;
import com.miniwallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(UserRepository userRepository, WalletRepository walletRepository,
                           TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse deposit(Long userId, DepositRequest request) {
        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setFromWalletId(null);
        transaction.setToWalletId(wallet.getId());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction = transactionRepository.save(transaction);

        return toTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse transfer(Long senderUserId, TransferRequest request) {
        User recipientUser = userRepository.findByEmail(request.getRecipientEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + request.getRecipientEmail()));

        if (senderUserId.equals(recipientUser.getId())) {
            throw new BusinessRuleException("Cannot transfer funds to yourself");
        }

        Wallet senderWallet = walletRepository.findByUserIdWithLock(senderUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found"));

        Wallet recipientWallet = walletRepository.findByUserIdWithLock(recipientUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient wallet not found"));

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            Transaction failedTx = new Transaction();
            failedTx.setFromWalletId(senderWallet.getId());
            failedTx.setToWalletId(recipientWallet.getId());
            failedTx.setAmount(request.getAmount());
            failedTx.setType(TransactionType.TRANSFER);
            failedTx.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(failedTx);

            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: " + senderWallet.getBalance()
                    + ", Requested: " + request.getAmount());
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        recipientWallet.setBalance(recipientWallet.getBalance().add(request.getAmount()));

        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        Transaction transaction = new Transaction();
        transaction.setFromWalletId(senderWallet.getId());
        transaction.setToWalletId(recipientWallet.getId());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction = transactionRepository.save(transaction);

        return toTransactionResponse(transaction);
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        return new TransactionResponse(transaction.getId(), transaction.getFromWalletId(),
                transaction.getToWalletId(), transaction.getAmount(), transaction.getType(),
                transaction.getStatus(), transaction.getCreatedAt());
    }
}
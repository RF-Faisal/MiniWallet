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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private TransactionRepository transactionRepository;

    private TransferService transferService;

    private User sender;
    private User recipient;
    private Wallet senderWallet;
    private Wallet recipientWallet;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(userRepository, walletRepository, transactionRepository);

        sender = new User();
        sender.setId(1L);
        sender.setName("Sender");
        sender.setEmail("sender@example.com");
        sender.setRole(Role.USER);

        recipient = new User();
        recipient.setId(2L);
        recipient.setName("Recipient");
        recipient.setEmail("recipient@example.com");
        recipient.setRole(Role.USER);

        senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setUser(sender);
        senderWallet.setBalance(new BigDecimal("500.00"));
        senderWallet.setCurrency("BDT");

        recipientWallet = new Wallet();
        recipientWallet.setId(2L);
        recipientWallet.setUser(recipient);
        recipientWallet.setBalance(new BigDecimal("100.00"));
        recipientWallet.setCurrency("BDT");
    }

    @Test
    @DisplayName("Should successfully transfer funds between wallets")
    void transferSuccess() {
        TransferRequest request = new TransferRequest();
        request.setRecipientEmail("recipient@example.com");
        request.setAmount(new BigDecimal("200.00"));

        when(userRepository.findByEmail("recipient@example.com")).thenReturn(Optional.of(recipient));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(recipientWallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TransactionResponse response = transferService.transfer(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo("200.00");
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        assertThat(response.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(senderWallet.getBalance()).isEqualByComparingTo("300.00");
        assertThat(recipientWallet.getBalance()).isEqualByComparingTo("300.00");

        verify(walletRepository).save(senderWallet);
        verify(walletRepository).save(recipientWallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when transferring to non-existent user")
    void transferToNonExistentUser() {
        TransferRequest request = new TransferRequest();
        request.setRecipientEmail("nonexistent@example.com");
        request.setAmount(new BigDecimal("100.00"));

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.transfer(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with email");
    }

    @Test
    @DisplayName("Should throw exception when transferring to self")
    void transferToSelf() {
        TransferRequest request = new TransferRequest();
        request.setRecipientEmail("sender@example.com");
        request.setAmount(new BigDecimal("100.00"));

        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));

        assertThatThrownBy(() -> transferService.transfer(1L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot transfer funds to yourself");
    }

    @Test
    @DisplayName("Should throw exception and create FAILED transaction when insufficient balance")
    void transferInsufficientBalance() {
        TransferRequest request = new TransferRequest();
        request.setRecipientEmail("recipient@example.com");
        request.setAmount(new BigDecimal("1000.00"));

        when(userRepository.findByEmail("recipient@example.com")).thenReturn(Optional.of(recipient));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(recipientWallet));

        assertThatThrownBy(() -> transferService.transfer(1L, request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance");

        verify(transactionRepository).save(argThat(tx ->
                tx.getStatus() == TransactionStatus.FAILED &&
                tx.getType() == TransactionType.TRANSFER));
    }

    @Test
    @DisplayName("Should successfully deposit funds")
    void depositSuccess() {
        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("500.00"));

        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(senderWallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TransactionResponse response = transferService.deposit(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo("500.00");
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        assertThat(response.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(senderWallet.getBalance()).isEqualByComparingTo("1000.00");

        verify(walletRepository).save(senderWallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when depositing to non-existent wallet")
    void depositWalletNotFound() {
        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("100.00"));

        when(walletRepository.findByUserIdWithLock(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.deposit(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Wallet not found");
    }
}
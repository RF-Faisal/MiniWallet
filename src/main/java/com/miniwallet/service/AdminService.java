package com.miniwallet.service;

import com.miniwallet.dto.response.*;
import com.miniwallet.entity.*;
import com.miniwallet.exception.ResourceNotFoundException;
import com.miniwallet.repository.TransactionRepository;
import com.miniwallet.repository.UserRepository;
import com.miniwallet.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public AdminService(UserRepository userRepository, WalletRepository walletRepository,
                        TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public PageResponse<UserResponse> listUsers(int page, int size, String role) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> userPage;
        if (role != null && !role.isEmpty()) {
            userPage = userRepository.findByRole(Role.valueOf(role.toUpperCase()), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        Page<UserResponse> responsePage = userPage.map(this::toUserResponse);
        return PageResponse.of(responsePage);
    }

    public UserWalletResponse getUserWithWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));

        return new UserWalletResponse(toUserResponse(user), toWalletResponse(wallet));
    }

    public PageResponse<TransactionResponse> listAllTransactions(
            int page, int size, Long userId, String type, String status,
            LocalDate fromDate, LocalDate toDate) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Transaction> transactionPage;

        boolean hasUserId = userId != null;
        boolean hasType = type != null && !type.isEmpty();
        boolean hasStatus = status != null && !status.isEmpty();
        boolean hasFromDate = fromDate != null;
        boolean hasToDate = toDate != null;

        if (hasUserId) {
            transactionPage = transactionRepository.findAllByUserId(userId, pageable);
        } else if (hasType && hasFromDate && hasToDate && hasStatus) {
            transactionPage = transactionRepository.findByTypeAndDateRangeAndStatus(
                    TransactionType.valueOf(type.toUpperCase()),
                    fromDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
                    toDate.atTime(OffsetTime.MAX).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime(),
                    TransactionStatus.valueOf(status.toUpperCase()),
                    pageable);
        } else if (hasType && hasFromDate && hasToDate) {
            transactionPage = transactionRepository.findByTypeAndDateRange(
                    TransactionType.valueOf(type.toUpperCase()),
                    fromDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
                    toDate.atTime(OffsetTime.MAX).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime(),
                    pageable);
        } else if (hasType && hasStatus) {
            transactionPage = transactionRepository.findByTypeAndStatus(
                    TransactionType.valueOf(type.toUpperCase()),
                    TransactionStatus.valueOf(status.toUpperCase()),
                    pageable);
        } else if (hasFromDate && hasToDate && hasStatus) {
            transactionPage = transactionRepository.findByDateRangeAndStatus(
                    fromDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
                    toDate.atTime(OffsetTime.MAX).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime(),
                    TransactionStatus.valueOf(status.toUpperCase()),
                    pageable);
        } else if (hasFromDate && hasToDate) {
            transactionPage = transactionRepository.findByDateRange(
                    fromDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
                    toDate.atTime(OffsetTime.MAX).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime(),
                    pageable);
        } else if (hasType) {
            transactionPage = transactionRepository.findByType(
                    TransactionType.valueOf(type.toUpperCase()), pageable);
        } else if (hasStatus) {
            transactionPage = transactionRepository.findByStatus(
                    TransactionStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            transactionPage = transactionRepository.findAll(pageable);
        }

        Page<TransactionResponse> responsePage = transactionPage.map(this::toTransactionResponse);
        return PageResponse.of(responsePage);
    }

    public StatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalTransactions = transactionRepository.countAllTransactions();
        double totalVolume = transactionRepository.totalVolume();
        long totalSuccessfulTransfers = transactionRepository.countSuccessfulTransfers();
        long totalFailedTransfers = transactionRepository.countFailedTransfers();

        return new StatsResponse(totalUsers, totalTransactions, totalVolume,
                totalSuccessfulTransfers, totalFailedTransfers);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getRole(), user.getCreatedAt());
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        return new WalletResponse(wallet.getId(), wallet.getBalance(), wallet.getCurrency(),
                wallet.getCreatedAt(), wallet.getUpdatedAt());
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        return new TransactionResponse(transaction.getId(), transaction.getFromWalletId(),
                transaction.getToWalletId(), transaction.getAmount(), transaction.getType(),
                transaction.getStatus(), transaction.getCreatedAt());
    }
}
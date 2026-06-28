package com.miniwallet.service;

import com.miniwallet.dto.response.PageResponse;
import com.miniwallet.dto.response.TransactionResponse;
import com.miniwallet.entity.Transaction;
import com.miniwallet.entity.TransactionType;
import com.miniwallet.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public PageResponse<TransactionResponse> getOwnTransactions(
            Long userId, int page, int size,
            String type, LocalDate fromDate, LocalDate toDate) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Transaction> transactionPage;

        boolean hasType = type != null && !type.isEmpty();
        boolean hasFromDate = fromDate != null;
        boolean hasToDate = toDate != null;

        if (hasType && hasFromDate && hasToDate) {
            transactionPage = transactionRepository.findByUserIdAndTypeAndDateRange(
                    userId, TransactionType.valueOf(type.toUpperCase()),
                    fromDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
                    toDate.atTime(OffsetTime.MAX).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime(),
                    pageable);
        } else if (hasType) {
            transactionPage = transactionRepository.findByUserIdAndType(
                    userId, TransactionType.valueOf(type.toUpperCase()), pageable);
        } else if (hasFromDate && hasToDate) {
            transactionPage = transactionRepository.findByUserIdAndDateRange(
                    userId,
                    fromDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime(),
                    toDate.atTime(OffsetTime.MAX).atZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime(),
                    pageable);
        } else {
            transactionPage = transactionRepository.findByUserId(userId, pageable);
        }

        Page<TransactionResponse> responsePage = transactionPage.map(this::toTransactionResponse);
        return PageResponse.of(responsePage);
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        return new TransactionResponse(transaction.getId(), transaction.getFromWalletId(),
                transaction.getToWalletId(), transaction.getAmount(), transaction.getType(),
                transaction.getStatus(), transaction.getCreatedAt());
    }
}
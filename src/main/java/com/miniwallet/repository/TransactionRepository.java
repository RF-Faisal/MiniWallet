package com.miniwallet.repository;

import com.miniwallet.entity.Transaction;
import com.miniwallet.entity.TransactionStatus;
import com.miniwallet.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.fromWalletId IN " +
           "(SELECT w.id FROM Wallet w WHERE w.user.id = :userId) " +
           "OR t.toWalletId IN (SELECT w.id FROM Wallet w WHERE w.user.id = :userId)")
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromWalletId IN (SELECT w.id FROM Wallet w WHERE w.user.id = :userId) " +
           "OR t.toWalletId IN (SELECT w.id FROM Wallet w WHERE w.user.id = :userId)) " +
           "AND t.type = :type")
    Page<Transaction> findByUserIdAndType(@Param("userId") Long userId,
                                          @Param("type") TransactionType type,
                                          Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromWalletId IN (SELECT w.id FROM Wallet w WHERE w.user.id = :userId) " +
           "OR t.toWalletId IN (SELECT w.id FROM Wallet w WHERE w.user.id = :userId)) " +
           "AND t.createdAt >= :fromDate AND t.createdAt <= :toDate")
    Page<Transaction> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                @Param("fromDate") OffsetDateTime fromDate,
                                                @Param("toDate") OffsetDateTime toDate,
                                                Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromWalletId IN (SELECT w.id FROM Wallet w WHERE w.user.id = :userId) " +
           "OR t.toWalletId IN (SELECT w.id FROM Wallet w WHERE w.user.id = :userId)) " +
           "AND t.type = :type AND t.createdAt >= :fromDate AND t.createdAt <= :toDate")
    Page<Transaction> findByUserIdAndTypeAndDateRange(@Param("userId") Long userId,
                                                       @Param("type") TransactionType type,
                                                       @Param("fromDate") OffsetDateTime fromDate,
                                                       @Param("toDate") OffsetDateTime toDate,
                                                       Pageable pageable);

    // Admin queries
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromWalletId IN (SELECT w.id FROM Wallet w WHERE w.user.id = :userId) " +
           "OR t.toWalletId IN (SELECT w.id FROM Wallet w WHERE w.user.id = :userId))")
    Page<Transaction> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    Page<Transaction> findByType(TransactionType type, Pageable pageable);

    Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.createdAt >= :fromDate AND t.createdAt <= :toDate")
    Page<Transaction> findByDateRange(@Param("fromDate") OffsetDateTime fromDate,
                                       @Param("toDate") OffsetDateTime toDate,
                                       Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.type = :type AND t.createdAt >= :fromDate AND t.createdAt <= :toDate")
    Page<Transaction> findByTypeAndDateRange(@Param("type") TransactionType type,
                                              @Param("fromDate") OffsetDateTime fromDate,
                                              @Param("toDate") OffsetDateTime toDate,
                                              Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.type = :type AND t.status = :status")
    Page<Transaction> findByTypeAndStatus(@Param("type") TransactionType type,
                                           @Param("status") TransactionStatus status,
                                           Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.createdAt >= :fromDate AND t.createdAt <= :toDate AND t.status = :status")
    Page<Transaction> findByDateRangeAndStatus(@Param("fromDate") OffsetDateTime fromDate,
                                                @Param("toDate") OffsetDateTime toDate,
                                                @Param("status") TransactionStatus status,
                                                Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.type = :type AND t.createdAt >= :fromDate AND t.createdAt <= :toDate AND t.status = :status")
    Page<Transaction> findByTypeAndDateRangeAndStatus(@Param("type") TransactionType type,
                                                       @Param("fromDate") OffsetDateTime fromDate,
                                                       @Param("toDate") OffsetDateTime toDate,
                                                       @Param("status") TransactionStatus status,
                                                       Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transaction t")
    long countAllTransactions();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = 'SUCCESS'")
    double totalVolume();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = 'TRANSFER' AND t.status = 'SUCCESS'")
    long countSuccessfulTransfers();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = 'TRANSFER' AND t.status = 'FAILED'")
    long countFailedTransfers();
}
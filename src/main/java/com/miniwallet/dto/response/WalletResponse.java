package com.miniwallet.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class WalletResponse {
    private Long id;
    private BigDecimal balance;
    private String currency;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public WalletResponse() {}

    public WalletResponse(Long id, BigDecimal balance, String currency, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id; this.balance = balance; this.currency = currency;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
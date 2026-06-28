package com.miniwallet.dto.response;

public class StatsResponse {
    private long totalUsers;
    private long totalTransactions;
    private double totalVolume;
    private long totalSuccessfulTransfers;
    private long totalFailedTransfers;

    public StatsResponse() {}

    public StatsResponse(long totalUsers, long totalTransactions, double totalVolume,
                          long totalSuccessfulTransfers, long totalFailedTransfers) {
        this.totalUsers = totalUsers; this.totalTransactions = totalTransactions;
        this.totalVolume = totalVolume; this.totalSuccessfulTransfers = totalSuccessfulTransfers;
        this.totalFailedTransfers = totalFailedTransfers;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(long totalTransactions) { this.totalTransactions = totalTransactions; }
    public double getTotalVolume() { return totalVolume; }
    public void setTotalVolume(double totalVolume) { this.totalVolume = totalVolume; }
    public long getTotalSuccessfulTransfers() { return totalSuccessfulTransfers; }
    public void setTotalSuccessfulTransfers(long totalSuccessfulTransfers) { this.totalSuccessfulTransfers = totalSuccessfulTransfers; }
    public long getTotalFailedTransfers() { return totalFailedTransfers; }
    public void setTotalFailedTransfers(long totalFailedTransfers) { this.totalFailedTransfers = totalFailedTransfers; }
}
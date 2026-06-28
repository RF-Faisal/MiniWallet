package com.miniwallet.dto.response;

public class UserWalletResponse {
    private UserResponse user;
    private WalletResponse wallet;

    public UserWalletResponse() {}

    public UserWalletResponse(UserResponse user, WalletResponse wallet) {
        this.user = user; this.wallet = wallet;
    }
		
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
    public WalletResponse getWallet() { return wallet; }
    public void setWallet(WalletResponse wallet) { this.wallet = wallet; }
}
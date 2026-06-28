package com.miniwallet.dto.response;

public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String accessTokenExpiry;

    public RefreshTokenResponse() {}

    public RefreshTokenResponse(String accessToken, String refreshToken, String accessTokenExpiry) {
        this.accessToken = accessToken; this.refreshToken = refreshToken; this.accessTokenExpiry = accessTokenExpiry;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getAccessTokenExpiry() { return accessTokenExpiry; }
    public void setAccessTokenExpiry(String accessTokenExpiry) { this.accessTokenExpiry = accessTokenExpiry; }
}
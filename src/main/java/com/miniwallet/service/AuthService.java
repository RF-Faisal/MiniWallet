package com.miniwallet.service;

import com.miniwallet.config.JwtService;
import com.miniwallet.dto.request.LoginRequest;
import com.miniwallet.dto.request.RefreshTokenRequest;
import com.miniwallet.dto.request.RegisterRequest;
import com.miniwallet.dto.response.AuthResponse;
import com.miniwallet.dto.response.RefreshTokenResponse;
import com.miniwallet.dto.response.UserResponse;
import com.miniwallet.entity.RefreshToken;
import com.miniwallet.entity.Role;
import com.miniwallet.entity.User;
import com.miniwallet.entity.Wallet;
import com.miniwallet.exception.BusinessRuleException;
import com.miniwallet.exception.ResourceNotFoundException;
import com.miniwallet.repository.RefreshTokenRepository;
import com.miniwallet.repository.UserRepository;
import com.miniwallet.repository.WalletRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, WalletRepository walletRepository,
                       RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("BDT");
        walletRepository.save(wallet);

        return toUserResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshTokenValue = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        saveRefreshToken(refreshTokenValue, user.getId());

        OffsetDateTime expiry = OffsetDateTime.now()
                .plusSeconds(jwtService.getAccessTokenExpiration() / 1000);

        return new AuthResponse(accessToken, refreshTokenValue,
                expiry.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                toUserResponse(user));
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessRuleException("Invalid or expired refresh token"));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new BusinessRuleException("Invalid or expired refresh token");
        }

        refreshTokenRepository.delete(storedToken);

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshTokenValue = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        saveRefreshToken(newRefreshTokenValue, user.getId());

        OffsetDateTime expiry = OffsetDateTime.now()
                .plusSeconds(jwtService.getAccessTokenExpiration() / 1000);

        return new RefreshTokenResponse(newAccessToken, newRefreshTokenValue,
                expiry.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    private void saveRefreshToken(String tokenValue, Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setUserId(userId);
        refreshToken.setExpiresAt(OffsetDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshToken);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getRole(), user.getCreatedAt());
    }
}
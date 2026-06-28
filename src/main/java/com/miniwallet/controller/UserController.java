package com.miniwallet.controller;

import com.miniwallet.dto.response.UserResponse;
import com.miniwallet.entity.User;
import com.miniwallet.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal User user) {
        UserResponse response = userService.getUserProfile(user.getId());
        return ResponseEntity.ok(response);
    }
}
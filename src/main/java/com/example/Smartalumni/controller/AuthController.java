package com.example.Smartalumni.controller;

import com.example.Smartalumni.dto.*;
import com.example.Smartalumni.service.AuthService;
import com.example.Smartalumni.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    // ✅ REGISTER
    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.<String>builder()
                .success(true)
                .message("Registration successful")
                .data(authService.register(request))
                .build();
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authService.login(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        authService.logout();
        return ApiResponse.<String>builder()
                .success(true)
                .message("Logout successful")
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestParam String email) {
        authService.sendResetLink(email);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Password reset link sent")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestParam String token,
            @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Password reset successful")
                .build();
    }
}
package com.example.Smartalumni.controller;

import com.example.Smartalumni.dto.ProfileRequest;
import com.example.Smartalumni.entity.UserProfile;
import com.example.Smartalumni.service.ProfileService;
import com.example.Smartalumni.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ✅ CREATE PROFILE
    @PostMapping("/{userId}")
    public ApiResponse<String> createProfile(@PathVariable Long userId,
                                             @RequestBody ProfileRequest request) {
        return ApiResponse.<String>builder()
                .success(true)
                .data(profileService.createProfile(userId, request))
                .build();
    }

    // ✅ GET PROFILE
    @GetMapping("/{userId}")
    public ApiResponse<UserProfile> viewProfile(@PathVariable Long userId) {
        return ApiResponse.<UserProfile>builder()
                .success(true)
                .data(profileService.viewProfile(userId))
                .build();
    }

    // ✅ UPDATE PROFILE
    @PutMapping("/{userId}")
    public ApiResponse<String> updateProfile(@PathVariable Long userId,
                                             @RequestBody ProfileRequest request) {
        return ApiResponse.<String>builder()
                .success(true)
                .data(profileService.updateProfile(userId, request))
                .build();
    }

    // ✅ DELETE PROFILE
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteProfile(@PathVariable Long userId) {
        return ApiResponse.<String>builder()
                .success(true)
                .data(profileService.deleteProfile(userId))
                .build();
    }
}
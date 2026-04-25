package com.example.Smartalumni.service;

import com.example.Smartalumni.dto.ProfileRequest;
import com.example.Smartalumni.entity.User;
import com.example.Smartalumni.entity.UserProfile;
import com.example.Smartalumni.exception.CustomException;
import com.example.Smartalumni.repository.UserProfileRepository;
import com.example.Smartalumni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    // ✅ CREATE PROFILE
    public String createProfile(Long userId, ProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));

        // Optional: prevent duplicate profile
        profileRepository.findByUser(user).ifPresent(p -> {
            throw new CustomException("Profile already exists");
        });

        UserProfile profile = UserProfile.builder()
                .user(user)
                .bio(request.getBio())
                .skills(request.getSkills()) // string (based on your DB)
                .resumeUrl(request.getResumeUrl())
                .currentCompany(request.getCurrentCompany())
                .currentRole(request.getCurrentRole())
                .experience(request.getExperience()) // string
                .build();

        profileRepository.save(profile);
        return "Profile created successfully";
    }

    // ✅ VIEW PROFILE
    public UserProfile viewProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));

        return profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found"));
    }

    // ✅ UPDATE PROFILE
    public String updateProfile(Long userId, ProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));

        UserProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found"));

        profile.setBio(request.getBio());
        profile.setSkills(request.getSkills());
        profile.setResumeUrl(request.getResumeUrl());
        profile.setCurrentCompany(request.getCurrentCompany());
        profile.setCurrentRole(request.getCurrentRole());
        profile.setExperience(request.getExperience());

        profileRepository.save(profile);
        return "Profile updated successfully";
    }

    // ✅ DELETE PROFILE
    public String deleteProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));

        UserProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found"));

        profileRepository.delete(profile);

        return "Profile deleted successfully";
    }
}
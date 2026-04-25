package com.example.Smartalumni.service;

import com.example.Smartalumni.dto.LoginRequest;
import com.example.Smartalumni.dto.RegisterRequest;
import com.example.Smartalumni.dto.AuthResponse;
import com.example.Smartalumni.entity.User;
import com.example.Smartalumni.enums.Role;
import com.example.Smartalumni.exception.CustomException;
import com.example.Smartalumni.repository.UserRepository;
import com.example.Smartalumni.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // ✅ REGISTER
    public String register(RegisterRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new CustomException("Email is required");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already registered");
        }

        if (request.getRole() == Role.ADMIN) {
            throw new CustomException("Admin registration is not allowed");
        }

        // ROLE VALIDATION
        if (request.getRole() == Role.STUDENT) {
            if (request.getDepartment() == null || request.getGraduationYear() == null) {
                throw new CustomException("STUDENT must have department and graduationYear");
            }
        }

        if (request.getRole() == Role.ALUMNI) {
            if (request.getJobTitle() == null || request.getCompany() == null) {
                throw new CustomException("ALUMNI must have jobTitle and company");
            }
        }

        // Alumni verification
        boolean isVerified = false;
        if (request.getRole() == Role.ALUMNI &&
                request.getEmail().endsWith("@gmail.com")) {
            isVerified = true;
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .department(request.getDepartment())
                .company(request.getCompany())
                .jobTitle(request.getJobTitle())
                .graduationYear(request.getGraduationYear())
                .verified(isVerified)
                .build();

        userRepository.save(user);

        return "User registered successfully. Please login.";
    }

    // ✅ LOGIN
    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new CustomException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));

        if (user.getRole() == Role.ALUMNI && !user.isVerified()) {
            throw new CustomException("Alumni not verified yet");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId());

        return AuthResponse.builder()
                .token(token)
                .message("Login successful")
                .role(user.getRole().name())
                .email(user.getEmail())
                .userId(user.getId())
                .fullName(user.getFullName())
                .build();
    }

    // ✅ LOGOUT
    public void logout() {
        // JWT is stateless → nothing to do
        // frontend just deletes token
    }

    // ✅ FORGOT PASSWORD
    public void sendResetLink(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        // TEMP: simulate reset link
        System.out.println("Reset link sent to: " + user.getEmail());
    }

    // ✅ RESET PASSWORD
    public void resetPassword(String token, String newPassword) {

        // TEMP: token = email (simple version)
        User user = userRepository.findByEmail(token)
                .orElseThrow(() -> new CustomException("Invalid token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
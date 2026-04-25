package com.example.Smartalumni.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String message;
    private String role;
    private String email;

    // ✅ Added fields
    private Long userId;
    private String fullName;
}
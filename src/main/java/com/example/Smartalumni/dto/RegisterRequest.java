package com.example.Smartalumni.dto;

import com.example.Smartalumni.dto.RegisterRequest;
import com.example.Smartalumni.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull
    private Role role;

    private String department;
    private String company;
    private String jobTitle;
    private String graduationYear;
}
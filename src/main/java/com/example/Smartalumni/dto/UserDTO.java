package com.example.Smartalumni.dto;

import com.example.Smartalumni.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String department;
    private String graduationYear;
    private String jobTitle;
    private String company;
    private Role role;
}
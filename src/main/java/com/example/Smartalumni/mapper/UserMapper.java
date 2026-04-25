package com.example.Smartalumni.mapper;

import com.example.Smartalumni.dto.UserDTO;
import com.example.Smartalumni.entity.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .department(user.getDepartment())
                .graduationYear(user.getGraduationYear())
                .jobTitle(user.getJobTitle())
                .company(user.getCompany())
                .role(user.getRole())
                .build();
    }
}
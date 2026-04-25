package com.example.Smartalumni.controller;

import com.example.Smartalumni.dto.UserDTO;
import com.example.Smartalumni.service.UserService;
import com.example.Smartalumni.util.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome Admin!";
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ApiResponse.<String>builder()
                .success(true)
                .message("User deleted successfully")
                .build();
    }
}
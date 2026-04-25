package com.example.Smartalumni.service;

import com.example.Smartalumni.dto.UserDTO;
import com.example.Smartalumni.entity.User;
import com.example.Smartalumni.enums.Role;
import com.example.Smartalumni.exception.CustomException;
import com.example.Smartalumni.mapper.UserMapper;
import com.example.Smartalumni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> getStudents() {
        return userRepository.findByRole(Role.STUDENT)
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public List<UserDTO> getAlumni() {
        return userRepository.findByRole(Role.ALUMNI)
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        return UserMapper.toDTO(user);
    }

    // DELETE USER
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));

        userRepository.delete(user);
    }
}
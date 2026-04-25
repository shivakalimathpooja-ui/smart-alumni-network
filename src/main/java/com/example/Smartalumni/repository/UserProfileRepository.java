package com.example.Smartalumni.repository;

import com.example.Smartalumni.entity.User;
import com.example.Smartalumni.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
}
package com.example.Smartalumni.config;

import com.example.Smartalumni.entity.User;
import com.example.Smartalumni.enums.Role;
import com.example.Smartalumni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ✅ Check if admin already exists
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {

            User admin = User.builder()
                    .fullName("Admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123")) // 🔥 encrypted
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);

            System.out.println("✅ Admin user created!");
        } else {
            System.out.println("ℹ️ Admin already exists");
        }
    }
}
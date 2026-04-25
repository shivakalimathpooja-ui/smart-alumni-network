package com.example.Smartalumni.entity;

import com.example.Smartalumni.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String department;

    private String graduationYear;

    private String jobTitle;

    private String company;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // ✅ ADD THIS
    @Column(nullable = false)
    private boolean verified;

    private String skills;
    private String location;
    private String higherEducation;
}
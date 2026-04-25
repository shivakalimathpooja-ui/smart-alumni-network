package com.example.Smartalumni.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Link with User
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String bio;
    private String skills; // simple for now (comma separated)

    private String resumeUrl;

    private String currentCompany;
    private String currentRole;

    private String experience; // e.g. "2 years"
}
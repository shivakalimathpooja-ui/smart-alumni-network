package com.example.Smartalumni.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    private String bio;
    private String skills;
    private String resumeUrl;
    private String currentCompany;
    private String currentRole;
    private String experience;
}

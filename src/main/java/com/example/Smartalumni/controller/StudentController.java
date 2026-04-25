package com.example.Smartalumni.controller;

import com.example.Smartalumni.dto.MentorshipDTO;
import com.example.Smartalumni.dto.UserDTO;
import com.example.Smartalumni.service.MentorshipService;
import com.example.Smartalumni.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin("*")
public class StudentController {

    @Autowired
    private final UserService userService;
    private final MentorshipService mentorshipService;

    @GetMapping("/alumni")
    public List<UserDTO> getAllAlumni() {
        return userService.getAlumni();
    }

    @PostMapping("/mentorship/request")
    public MentorshipDTO requestMentorship(@RequestBody MentorshipDTO dto) {
        return mentorshipService.createRequest(dto);
    }
}
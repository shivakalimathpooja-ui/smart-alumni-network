package com.example.Smartalumni.controller;

import com.example.Smartalumni.dto.MentorshipDTO;
import com.example.Smartalumni.entity.Mentorship;
import com.example.Smartalumni.service.MentorshipService;
import com.example.Smartalumni.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    // ✅ CREATE REQUEST (Student)
    @PostMapping("/request")
    public ApiResponse<MentorshipDTO> request(@RequestBody MentorshipDTO dto) {
        return ApiResponse.<MentorshipDTO>builder()
                .success(true)
                .data(mentorshipService.createRequest(dto))
                .build();
    }

    // 📥 STUDENT VIEW
    @GetMapping("/student/my-requests")
    public ApiResponse<List<Mentorship>> getStudentRequests() {
        return ApiResponse.<List<Mentorship>>builder()
                .success(true)
                .data(mentorshipService.getMyRequests())
                .build();
    }

    // 📥 MENTOR VIEW
    @GetMapping("/mentor/my-requests")
    public ApiResponse<List<Mentorship>> getMentorRequests() {
        return ApiResponse.<List<Mentorship>>builder()
                .success(true)
                .data(mentorshipService.getMentorRequests())
                .build();
    }

    // ✅ ACCEPT + SCHEDULE (Mentor)
    @PutMapping("/accept-and-schedule/{id}")
    public ApiResponse<String> acceptAndSchedule(
            @PathVariable Long id,
            @RequestBody MentorshipDTO dto) {

        return ApiResponse.<String>builder()
                .success(true)
                .data(mentorshipService.acceptAndSchedule(id, dto))
                .build();
    }

    // ❌ REJECT (Mentor)
    @PutMapping("/reject/{id}")
    public ApiResponse<String> reject(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .success(true)
                .data(mentorshipService.rejectRequest(id))
                .build();
    }

    // 📅 SCHEDULE SESSION (Mentor)
    @PutMapping("/schedule/{id}")
    public ApiResponse<String> schedule(@PathVariable Long id,
                                        @RequestBody MentorshipDTO dto) {

        return ApiResponse.<String>builder()
                .success(true)
                .data(mentorshipService.scheduleSession(id, dto))
                .build();
    }

    // ⭐ ADD FEEDBACK (Student)
    @PostMapping("/feedback/{id}")
    public ApiResponse<String> feedback(@PathVariable Long id,
                                       @RequestBody MentorshipDTO dto) {

        return ApiResponse.<String>builder()
                .success(true)
                .data(mentorshipService.addFeedback(id, dto))
                .build();
    }
}
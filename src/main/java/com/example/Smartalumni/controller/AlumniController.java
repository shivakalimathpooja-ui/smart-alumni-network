package com.example.Smartalumni.controller;

import com.example.Smartalumni.entity.User;
import com.example.Smartalumni.service.AlumniService;
import com.example.Smartalumni.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AlumniController {

    private final AlumniService alumniService;

    // ✅ Get all alumni
    @GetMapping("/all")
    public ApiResponse<List<User>> getAllAlumni() {
        return ApiResponse.<List<User>>builder()
                .success(true)
                .data(alumniService.searchAlumni())
                .build();
    }

    // ✅ Filter by Domain (job title)
    @GetMapping("/domain")
    public ApiResponse<List<User>> domain(@RequestParam String value) {
        return ApiResponse.<List<User>>builder()
                .success(true)
                .data(alumniService.filterByDomain(value))
                .build();
    }

    // ✅ Filter by Skills
    @GetMapping("/skills")
    public ApiResponse<List<User>> skills(@RequestParam String value) {
        return ApiResponse.<List<User>>builder()
                .success(true)
                .data(alumniService.filterBySkills(value))
                .build();
    }

    // ✅ Filter by Company
    @GetMapping("/company")
    public ApiResponse<List<User>> company(@RequestParam String value) {
        return ApiResponse.<List<User>>builder()
                .success(true)
                .data(alumniService.filterByCompany(value))
                .build();
    }

    // ✅ Filter by Location
    @GetMapping("/location")
    public ApiResponse<List<User>> location(@RequestParam String value) {
        return ApiResponse.<List<User>>builder()
                .success(true)
                .data(alumniService.filterByLocation(value))
                .build();
    }

    // ✅ Filter by Batch
    @GetMapping("/batch")
    public ApiResponse<List<User>> batch(@RequestParam String value) {
        return ApiResponse.<List<User>>builder()
                .success(true)
                .data(alumniService.filterByBatch(value))
                .build();
    }

    // ✅ Filter by Education
    @GetMapping("/education")
    public ApiResponse<List<User>> education(@RequestParam String value) {
        return ApiResponse.<List<User>>builder()
                .success(true)
                .data(alumniService.filterByEducation(value))
                .build();
    }

    // ✅ Filter by Department
    @GetMapping("/department")
    public ApiResponse<List<User>> department(@RequestParam String value) {
        return ApiResponse.<List<User>>builder()
                .success(true)
                .data(alumniService.filterByDepartment(value))
                .build();
    }

    // 🔥 COMBINED SEARCH (MAIN API)
    @GetMapping("/search")
    public ApiResponse<List<User>> search(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) String domain) {

        return ApiResponse.<List<User>>builder()
                .success(true)
                .data(alumniService.searchAlumniAdvanced(
                        company, department, skill, location, batch, education, domain))
                .build();
    }
}
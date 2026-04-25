package com.example.Smartalumni.controller;

import com.example.Smartalumni.dto.JobApplyRequest;
import com.example.Smartalumni.dto.JobRequest;
import com.example.Smartalumni.entity.JobOpportunity;
import com.example.Smartalumni.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // ✅ Post Job
    @PostMapping
    public String post(@RequestBody JobRequest request) {
        return jobService.postJob(request);
    }

    // ✅ Apply Job
    @PostMapping("/apply")
    public String apply(@RequestBody JobApplyRequest request) {
        return jobService.apply(request.getJobId(), request.getUserId());
    }

    @GetMapping
public List<JobOpportunity> getAllJobs() {
    return jobService.getAllJobs();
}
}
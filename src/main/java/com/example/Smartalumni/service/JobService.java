package com.example.Smartalumni.service;

import com.example.Smartalumni.dto.JobRequest; // ✅ ADD THIS
import com.example.Smartalumni.entity.JobOpportunity;
import com.example.Smartalumni.repository.JobOpportunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobOpportunityRepository jobRepository;

    // ✅ postJobOpportunity
    public String postJob(JobRequest request) {
        JobOpportunity job = JobOpportunity.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .description(request.getDescription())
                .build();

        jobRepository.save(job);
        return "Job posted";
    }

    // ✅ applyForOpportunity
    public String apply(Long jobId, Long userId) {
        return "User " + userId + " applied for job " + jobId;
    }

    public List<JobOpportunity> getAllJobs() {
        return jobRepository.findAll();
    }
}

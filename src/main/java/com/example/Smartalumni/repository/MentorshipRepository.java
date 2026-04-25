package com.example.Smartalumni.repository;

import com.example.Smartalumni.entity.Mentorship;
import com.example.Smartalumni.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorshipRepository extends JpaRepository<Mentorship, Long> {

    List<Mentorship> findByStudentId(Long studentId);

    List<Mentorship> findByMentorId(Long mentorId);

    // List<Mentorship> findByStatus(Status status);
}
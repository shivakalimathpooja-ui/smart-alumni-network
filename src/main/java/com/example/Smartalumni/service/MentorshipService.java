package com.example.Smartalumni.service;

import com.example.Smartalumni.dto.MentorshipDTO;
import com.example.Smartalumni.entity.Mentorship;
import com.example.Smartalumni.entity.User;
import com.example.Smartalumni.enums.Role;
import com.example.Smartalumni.enums.Status;
import com.example.Smartalumni.exception.CustomException;
import com.example.Smartalumni.repository.MentorshipRepository;
import com.example.Smartalumni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

        private final MentorshipRepository mentorshipRepository;
        private final UserRepository userRepository;

        // 🔐 GET LOGGED-IN USER
        private User getLoggedInUser() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = auth.getName();

                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new CustomException("User not found"));
        }

        // ✅ CREATE REQUEST (Student only)
        public MentorshipDTO createRequest(MentorshipDTO dto) {

                User student = getLoggedInUser();

                if (student.getRole() != Role.STUDENT) {
                        throw new CustomException("Only students can request mentorship");
                }

                User mentor = userRepository.findById(dto.getMentorId())
                                .orElseThrow(() -> new CustomException("Mentor not found"));

                if (mentor.getRole() != Role.ALUMNI) {
                        throw new CustomException("Only alumni can be mentors");
                }

                Mentorship mentorship = Mentorship.builder()
                                .student(student)
                                .mentor(mentor)
                                .topic(dto.getTopic())
                                .message(dto.getMessage())
                                .status(Status.PENDING)
                                .build();

                mentorshipRepository.save(mentorship);

                return MentorshipDTO.builder()
                                .id(mentorship.getId())
                                .mentorId(mentor.getId())
                                .topic(mentorship.getTopic())
                                .message(mentorship.getMessage())
                                .status(mentorship.getStatus())
                                .build();
        }

        // ✅ ACCEPT + SCHEDULE (COMBINED)
        public String acceptAndSchedule(Long id, MentorshipDTO dto) {

                User mentor = getLoggedInUser();

                if (mentor.getRole() != Role.ALUMNI) {
                        throw new CustomException("Only mentor can accept and schedule");
                }

                Mentorship m = mentorshipRepository.findById(id)
                                .orElseThrow(() -> new CustomException("Request not found"));

                // 🔒 check ownership
                if (!m.getMentor().getId().equals(mentor.getId())) {
                        throw new CustomException("You are not assigned to this request");
                }

                // ✅ prevent double action
                if (m.getStatus() != Status.PENDING) {
                        throw new CustomException("Request already processed");
                }

                // ✅ ACCEPT
                m.setStatus(Status.APPROVED);

                // ✅ prevent rescheduling
                if (m.getScheduledAt() != null) {
                        throw new CustomException("Session already scheduled");
                }

                // ✅ validation
                if ("ONLINE".equalsIgnoreCase(dto.getMeetingType()) &&
                                (dto.getMeetingLink() == null || dto.getMeetingLink().isBlank())) {
                        throw new CustomException("Meeting link required");
                }

                if ("OFFLINE".equalsIgnoreCase(dto.getMeetingType()) &&
                                (dto.getLocation() == null || dto.getLocation().isBlank())) {
                        throw new CustomException("Location required");
                }

                // ✅ SCHEDULE
                m.setMeetingType(dto.getMeetingType());
                m.setMeetingLink(dto.getMeetingLink());
                m.setLocation(dto.getLocation());
                m.setScheduledAt(dto.getScheduledAt());

                mentorshipRepository.save(m);

                return "Request accepted and session scheduled";
        }

        // ❌ REJECT REQUEST
        public String rejectRequest(Long id) {

                User mentor = getLoggedInUser();

                if (mentor.getRole() != Role.ALUMNI) {
                        throw new CustomException("Only mentor can reject");
                }

                Mentorship m = mentorshipRepository.findById(id)
                                .orElseThrow(() -> new CustomException("Request not found"));

                if (!m.getMentor().getId().equals(mentor.getId())) {
                        throw new CustomException("Not your request");
                }

                // ✅ prevent double action
                if (m.getStatus() != Status.PENDING) {
                        throw new CustomException("Request already processed");
                }

                m.setStatus(Status.REJECTED);
                mentorshipRepository.save(m);

                return "Request rejected";
        }

        // 📥 STUDENT VIEW
        public List<Mentorship> getMyRequests() {

                User student = getLoggedInUser();

                if (student.getRole() != Role.STUDENT) {
                        throw new CustomException("Only student can view this");
                }

                return mentorshipRepository.findByStudentId(student.getId());
        }

        // 📥 MENTOR VIEW
        public List<Mentorship> getMentorRequests() {

                User mentor = getLoggedInUser();

                if (mentor.getRole() != Role.ALUMNI) {
                        throw new CustomException("Only mentor can view this");
                }

                return mentorshipRepository.findByMentorId(mentor.getId());
        }

        // 📅 SCHEDULE SESSION
        public String scheduleSession(Long id, MentorshipDTO dto) {

                User mentor = getLoggedInUser();

                if (mentor.getRole() != Role.ALUMNI) {
                        throw new CustomException("Only mentor can schedule session");
                }

                Mentorship m = mentorshipRepository.findById(id)
                                .orElseThrow(() -> new CustomException("Request not found"));

                if (!m.getMentor().getId().equals(mentor.getId())) {
                        throw new CustomException("You are not assigned to this request");
                }

                if (m.getStatus() != Status.APPROVED) {
                        throw new CustomException("Accept request before scheduling");
                }

                // ✅ prevent multiple scheduling
                if (m.getScheduledAt() != null) {
                        throw new CustomException("Session already scheduled");
                }

                // ✅ safe checks
                if ("ONLINE".equalsIgnoreCase(dto.getMeetingType()) &&
                                (dto.getMeetingLink() == null || dto.getMeetingLink().isBlank())) {
                        throw new CustomException("Meeting link required");
                }

                if ("OFFLINE".equalsIgnoreCase(dto.getMeetingType()) &&
                                (dto.getLocation() == null || dto.getLocation().isBlank())) {
                        throw new CustomException("Location required");
                }

                m.setMeetingType(dto.getMeetingType());
                m.setMeetingLink(dto.getMeetingLink());
                m.setLocation(dto.getLocation());
                m.setScheduledAt(dto.getScheduledAt());

                mentorshipRepository.save(m);

                return "Session scheduled successfully";
        }

        // ⭐ ADD FEEDBACK
        public String addFeedback(Long id, MentorshipDTO dto) {

                User student = getLoggedInUser();

                if (student.getRole() != Role.STUDENT) {
                        throw new CustomException("Only student can give feedback");
                }

                Mentorship m = mentorshipRepository.findById(id)
                                .orElseThrow(() -> new CustomException("Request not found"));

                if (!m.getStudent().getId().equals(student.getId())) {
                        throw new CustomException("This is not your session");
                }

                if (m.getScheduledAt() == null) {
                        throw new CustomException("Session not scheduled yet");
                }

                // ✅ validation
                if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
                        throw new CustomException("Rating must be between 1 and 5");
                }

                if (dto.getFeedback() == null || dto.getFeedback().isBlank()) {
                        throw new CustomException("Feedback cannot be empty");
                }

                m.setFeedback(dto.getFeedback());
                m.setRating(dto.getRating());

                mentorshipRepository.save(m);

                return "Feedback added successfully";
        }
}
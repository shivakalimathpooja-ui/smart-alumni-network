package com.example.Smartalumni.dto;

import com.example.Smartalumni.enums.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorshipDTO {

    private Long id;
    private String topic;
    private String message;
    private Long studentId;
    private Long mentorId;
    private Status status;

    // (for scheduling)
    private String meetingType;
    private String meetingLink;
    private String location;
    private String scheduledAt;

    // (for feedback later)
    private String feedback;
    private Integer rating;
}
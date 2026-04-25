package com.example.Smartalumni.entity;

import com.example.Smartalumni.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mentorships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mentorship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String message;

    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "mentor_id") 
    private User mentor;

    @Enumerated(EnumType.STRING)
    private Status status;

     //  Session
    private String meetingType; // ONLINE / OFFLINE
    private String meetingLink;
    private String location;
    private String scheduledAt;

    //  Feedback
    private String feedback;
    private Integer rating;

}
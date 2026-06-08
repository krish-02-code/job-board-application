package com.jobboard.jobboardapplication.job.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String requiredSkills;      // "Java, Spring Boot, Kafka"

    private String salaryRange;         // "10-15 LPA"

    @Enumerated(EnumType.STRING)
    private JobStatus status;           // OPEN or CLOSED

    private String postedByEmail;       // email of company who posted

    @CreationTimestamp
    private LocalDateTime postedAt;
}
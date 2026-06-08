package com.jobboard.jobboardapplication.matching.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "matchingResult")
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String candidateEmail;

    @Column(nullable = false)
    private Long jobId;             // to fetch the job details

    @Column(nullable = false)
    private Integer matchScore;     // 0 - 100 job score

    @Column(length = 2000)
    private String feedback;           // Gemini explanation

    private LocalDateTime matchedAt;
}

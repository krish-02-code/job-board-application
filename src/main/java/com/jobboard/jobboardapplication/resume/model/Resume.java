package com.jobboard.jobboardapplication.resume.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resumes")
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String candidateEmail;

    @Column(nullable = false)
    private String s3Key;              // path in S3 bucket

    @Column(nullable = false)
    private String fileName;           // original file name

    @Column(nullable = false, length = 10000)
    private String extractedText;      // text extracted by PDFBox

    private LocalDateTime uploadedAt;
}

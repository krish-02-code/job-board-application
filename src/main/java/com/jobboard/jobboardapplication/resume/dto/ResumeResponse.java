package com.jobboard.jobboardapplication.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ResumeResponse {
    private Long id;
    private String candidateEmail;
    private String fileName;
    private LocalDateTime uploadedAt;
    private String message;
}

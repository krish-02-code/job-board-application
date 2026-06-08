package com.jobboard.jobboardapplication.resume.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeUploadEvent {
    private String candidateEmail;  // required
    private String extractedText;   // matching-service needs this
    private String s3Key;           // used by matching service to download resume for future
}

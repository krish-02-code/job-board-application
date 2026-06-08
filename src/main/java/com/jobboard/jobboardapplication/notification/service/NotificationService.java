package com.jobboard.jobboardapplication.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    // When a new job is posted
    public void notifyNewJob(String title, String location,String requiredSkills, Long jobId) {
        log.info("NEW JOB ALERT");
        log.info("Title    : {}", title);
        log.info("Location : {}", location);
        log.info("Skills   : {}", requiredSkills);
        log.info("Job ID   : {}", jobId);
    }

    // When a resume is uploaded
    public void notifyResumeUploaded(String candidateEmail, String s3Key) {
        log.info("RESUME UPLOADED");
        log.info("Candidate : {}", candidateEmail);
        log.info("S3 Key    : {}", s3Key);
    }
}

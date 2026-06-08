package com.jobboard.jobboardapplication.job.dto;

import com.jobboard.jobboardapplication.job.model.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String company;
    private String location;
    private String requiredSkills;
    private String salaryRange;
    private String postedByEmail;
    private LocalDateTime postedAt;
    private JobStatus status;

}

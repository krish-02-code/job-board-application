package com.jobboard.jobboardapplication.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    private String title;
    private String description;
    private String company;
    private String location;
    private String requiredSkills;
    private String salaryRange;
}

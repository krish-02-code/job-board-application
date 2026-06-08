package com.jobboard.jobboardapplication.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private String candidateEmail;
    private Long jobId;
    private Integer matchScore;
    private String feedback;
    private boolean fromCache;
}

package com.jobboard.jobboardapplication.matching.controller;

import com.jobboard.jobboardapplication.matching.dto.MatchResponse;
import com.jobboard.jobboardapplication.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
@Slf4j
public class MatchingController {

    private final MatchingService matchingService;

    @GetMapping("/{jobId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<MatchResponse>getScore(@PathVariable Long jobId, @AuthenticationPrincipal String email){
        log.info("Match score request from: {} for job: {}", email, jobId);
        return ResponseEntity.ok(matchingService.getMatchScore(email, jobId));
    }
}

package com.jobboard.jobboardapplication.job.controller;

import com.jobboard.jobboardapplication.job.Service.JobService;
import com.jobboard.jobboardapplication.job.dto.JobRequest;
import com.jobboard.jobboardapplication.job.dto.JobResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
@Slf4j
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // post a job only ny the company

    @PostMapping("/post")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobResponse>addJob(
            @RequestBody JobRequest jobRequest,
            @AuthenticationPrincipal String email){
        log.info("Job post request from : {}", email);
        return ResponseEntity.ok(jobService.postJob(jobRequest,email));
    }

    //get all jobs for candidates

    @GetMapping("/all")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Page<JobResponse>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getAllOpenJobs(page, size));
    }

    //Search Jobs CANDIDATE only
    @GetMapping("/search")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<JobResponse>> searchJobs(@RequestParam String keyword) {
        return ResponseEntity.ok(jobService.searchJobs(keyword));
    }

    //Get Single Job both roles
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    //Get My Posted Jobs COMPANY only
    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<JobResponse>> getMyJobs(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(jobService.getMyPostedJobs(email));
    }

    // Close a Job COMPANY only
    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobResponse> closeJob(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(jobService.closeJob(id, email));
    }


}

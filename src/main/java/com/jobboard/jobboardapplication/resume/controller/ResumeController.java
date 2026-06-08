package com.jobboard.jobboardapplication.resume.controller;

import com.jobboard.jobboardapplication.resume.dto.ResumeResponse;
import com.jobboard.jobboardapplication.resume.model.Resume;
import com.jobboard.jobboardapplication.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
@Slf4j
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    // upload the resume candidate only

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResumeResponse>upload(
            @RequestPart("file")MultipartFile file,
            @AuthenticationPrincipal String email) throws IOException {
        log.info("Resume upload request for : {}",email);
        return ResponseEntity.ok(resumeService.uploadResume(file,email));
    }

    @GetMapping("/my-resume")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ResumeResponse>getAll(@AuthenticationPrincipal String email){
        return ResponseEntity.ok(resumeService.getMyResume(email));
    }

}

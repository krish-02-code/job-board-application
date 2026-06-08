package com.jobboard.jobboardapplication.job.Service;

import com.jobboard.jobboardapplication.ExceptionHandler.ResourceNotFoundException;
import com.jobboard.jobboardapplication.job.Repository.JobRepository;
import com.jobboard.jobboardapplication.job.dto.JobRequest;
import com.jobboard.jobboardapplication.job.dto.JobResponse;
import com.jobboard.jobboardapplication.job.event.JobPostedEvent;
import com.jobboard.jobboardapplication.job.model.Job;
import com.jobboard.jobboardapplication.job.model.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;

    private final KafkaTemplate<String, JobPostedEvent>kafkaTemplate;

    private final String JOB_POSTED_TOPIC = "job.posted";


    public JobResponse postJob(JobRequest jobRequest, String email) {
        Job job = Job.builder()
                .title(jobRequest.getTitle())
                .description(jobRequest.getDescription())
                .company(jobRequest.getCompany())
                .location(jobRequest.getLocation())
                .requiredSkills(jobRequest.getRequiredSkills())
                .salaryRange(jobRequest.getSalaryRange())
                .postedAt(LocalDateTime.now())
                .postedByEmail(email)
                .status(JobStatus.OPEN)
                .build();

        jobRepository.save(job);
        log.info("Job posted by {} : {}", email, job.getTitle());

        JobPostedEvent event = new JobPostedEvent(
                job.getId(),
                job.getTitle(),
                job.getRequiredSkills(),
                job.getLocation()
        );
        kafkaTemplate.send(JOB_POSTED_TOPIC, String.valueOf(job.getId()), event);
        log.info("JobPostedEvent published for job : {}", job.getId());

        return mapToResponse(job);
    }

    public Page<JobResponse> getAllOpenJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepository.findByStatus(JobStatus.OPEN, pageable)
                .map(this::mapToResponse);
    }

    public List<JobResponse> searchJobs(String keyword) {
        return jobRepository.searchByKeyword(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return mapToResponse(job);
    }

    public List<JobResponse> getMyPostedJobs(String email) {
        return jobRepository.findByPostedByEmail(email).stream().map(this :: mapToResponse).toList();
    }

    public JobResponse closeJob(Long id, String email) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getPostedByEmail().equals(email)) {
            throw new RuntimeException("You can only close your own jobs");
        }

        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
        log.info("Job {} closed by {}", id, email);
        return mapToResponse(job);
    }

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .company(job.getCompany())
                .location(job.getLocation())
                .requiredSkills(job.getRequiredSkills())
                .salaryRange(job.getSalaryRange())
                .status(job.getStatus())
                .postedByEmail(job.getPostedByEmail())
                .postedAt(job.getPostedAt())
                .build();
    }
}

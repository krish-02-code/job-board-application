package com.jobboard.jobboardapplication.job.Repository;

import com.jobboard.jobboardapplication.job.model.Job;
import com.jobboard.jobboardapplication.job.model.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job,Long> {
    Page<Job> findByStatus(JobStatus status, Pageable pageable);  // get all open jobs
    List<Job>findByPostedByEmail(String email);  // get all posted by specific email

    // Search by keyword in title or description or skills
    @Query("SELECT j FROM Job j WHERE " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Job> searchByKeyword(@Param("keyword") String keyword);

}

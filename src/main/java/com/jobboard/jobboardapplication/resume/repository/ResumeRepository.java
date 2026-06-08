package com.jobboard.jobboardapplication.resume.repository;

import com.jobboard.jobboardapplication.resume.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume,Long> {
    boolean existsByCandidateEmail(String email);
    Optional<Resume> findByCandidateEmail(String email);
}

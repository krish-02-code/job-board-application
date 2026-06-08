package com.jobboard.jobboardapplication.matching.repository;

import com.jobboard.jobboardapplication.matching.model.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchingRepository extends JpaRepository<MatchResult,Long> {
    Optional<MatchResult> findByCandidateEmailAndJobId(String email, Long jobId);
}

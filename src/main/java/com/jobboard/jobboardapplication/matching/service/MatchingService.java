package com.jobboard.jobboardapplication.matching.service;

import com.jobboard.jobboardapplication.ExceptionHandler.ResourceNotFoundException;
import com.jobboard.jobboardapplication.ExceptionHandler.UserNotFoundException;
import com.jobboard.jobboardapplication.job.Repository.JobRepository;
import com.jobboard.jobboardapplication.job.model.Job;
import com.jobboard.jobboardapplication.matching.dto.MatchResponse;
import com.jobboard.jobboardapplication.matching.model.MatchResult;
import com.jobboard.jobboardapplication.matching.repository.MatchingRepository;
import com.jobboard.jobboardapplication.resume.model.Resume;
import com.jobboard.jobboardapplication.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final GeminiService geminiService;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final MatchingRepository matchingRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String MATCH_CACHE_PREFIX = "match:";
    private static final int CACHE_TTL_HOURS = 24;


    public MatchResponse getMatchScore(String email, Long jobId) {

        String cacheKey = MATCH_CACHE_PREFIX + email + ":" + jobId;
        String cacheScore = redisTemplate.opsForValue().get(cacheKey);

        if(cacheScore != null){
            log.info("Cache Hit for match : {}",cacheKey);
            MatchResult cached = matchingRepository.findByCandidateEmailAndJobId(email,jobId)
                    .orElseThrow(()-> new RuntimeException("Match not found !"));

            return new MatchResponse(
                    email,jobId,
                    cached.getMatchScore(),
                    cached.getFeedback(),
                    true
            );
        }
        log.info("Cache MISS for match: {}", cacheKey);

        // 2 : fetch resume and job
        Resume resume = resumeRepository.findByCandidateEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("Resume not found !"));

        Job job = jobRepository.findById(jobId).orElseThrow(()-> new ResourceNotFoundException("Job not found"));

        // 3 : call gemini api
        log.info("Calling gemini api for match : {} - job : {}",email,jobId);
        String geminiResponse = geminiService.analyzeMatch(
                resume.getExtractedText(),job.getDescription(),job.getRequiredSkills());


        // 4:parse gemini response
        int score = parseScore(geminiResponse);
        String feedback = parseFeedback(geminiResponse);
        log.info("Gemini returned score: {} for candidate: {}", score, email);

        //5 : save to db
        MatchResult matchResult = matchingRepository
                .findByCandidateEmailAndJobId(email, jobId)
                .orElse(new MatchResult());

        matchResult.setCandidateEmail(email);
        matchResult.setJobId(jobId);
        matchResult.setMatchScore(score);
        matchResult.setFeedback(feedback);
        matchResult.setMatchedAt(LocalDateTime.now());
        matchingRepository.save(matchResult);

        redisTemplate.opsForValue().set(cacheKey,String.valueOf(score),CACHE_TTL_HOURS, TimeUnit.HOURS);
        log.info("Match score cached for : {}",cacheKey);
        return new MatchResponse(email,jobId,score,feedback,false);
    }

    private String parseFeedback(String geminiResponse) {
        try{
            for (String line : geminiResponse.split("\n")) {
                if (line.startsWith("FEEDBACK:")) {
                    return line.replace("FEEDBACK:", "").trim();
                }
            }
        }catch(Exception e){
            log.warn("Could not parse feedback from Gemini response");
        }
        return "Unable to generate feedback";
    }

    private int parseScore(String geminiResponse) {
        try{
            for(String line : geminiResponse.split("\n")){
                 if(line.startsWith("SCORE:")){
                     String numberStr = line.replace("SCORE:","").trim();
                     return Integer.parseInt(numberStr);
                 }
            }
        }catch (Exception e){
            log.warn("Could not parse score from Gemini response , defaulting to 0");
        }
        return 0;
    }
}

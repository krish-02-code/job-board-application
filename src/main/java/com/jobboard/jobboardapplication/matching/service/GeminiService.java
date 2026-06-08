package com.jobboard.jobboardapplication.matching.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.api-url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String analyzeMatch(String resumeText, String jobDescription, String requiredSkills) {
        try {

            // build prompt
            String prompt = buildPrompt(resumeText, jobDescription, requiredSkills);

            // build request body
            String requestBody = """
                    {
                      "contents":[{
                      "parts":[{
                        "text" : "%s"
                         }]
                      }]
                    }
                    """.formatted(prompt.replace("\"","\\\"")
                    .replace("\n","\\n"));

            //call gemini api
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String>response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
            log.info("Gemini API response status: {}", response.statusCode());

            // Extract text from response

            JsonNode root = objectMapper.readTree(response.body());
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();


        } catch (Exception e){
            log.error("Gemini API call failed: {}", e.getMessage());
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage());
        }
    }

    private String buildPrompt(String resumeText, String jobDescription, String requiredSkills) {

        return """
                You are an expert HR recruiter and resume analyzer.
                
                Analyze the following resume against the job description and required skills.
                
                RESUME:
                %s
                
                JOB DESCRIPTION:
                %s
                
                REQUIRED SKILLS:
                %s
                
                Provide your response in EXACTLY this format:
                SCORE: [number between 0-100]
                FEEDBACK: [2-3 sentences explaining the match, strengths, and gaps]
                """.formatted(resumeText, jobDescription, requiredSkills);
    }
}

package com.jobboard.jobboardapplication.resume.service;

import com.jobboard.jobboardapplication.ExceptionHandler.ResourceNotFoundException;
import com.jobboard.jobboardapplication.resume.dto.ResumeResponse;
import com.jobboard.jobboardapplication.resume.event.ResumeUploadEvent;
import com.jobboard.jobboardapplication.resume.model.Resume;
import com.jobboard.jobboardapplication.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeService {

    private final S3Client s3Client;
    private final KafkaTemplate<String, ResumeUploadEvent>kafkaTemplate;
    private final ResumeRepository resumeRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final String RESUME_UPLOADED_TOPIC = "resume.uploaded";

    public ResumeResponse uploadResume(MultipartFile file, String email) throws IOException {

        byte[] fileBytes = file.getBytes();

        //step 1 : extract the text from pdf
        String extractedText = extractTextPdf(fileBytes);

        //step 2 : upload the pdf to s3
        String S3key = "resumes/"+email+"/"+ UUID.randomUUID()+"_"+file.getOriginalFilename();
        uploadToS3(fileBytes,file.getContentType(),S3key);
        log.info("Resume uploaded to s3 successfully with key : {}",S3key);

        //step 3 : save or update in db
        Resume resume;
        if(resumeRepository.existsByCandidateEmail(email)){
            resume = resumeRepository.findByCandidateEmail(email).orElseThrow(()-> new ResourceNotFoundException("Resume not exists"));

            //delete the existing resume in s3 first
            deleteFromS3(resume.getS3Key());

            resume.setFileName(file.getOriginalFilename());
            resume.setS3Key(S3key);
            resume.setExtractedText(extractedText);
            resume.setUploadedAt(LocalDateTime.now());
            log.info("Existing resume updated for candidate: {}", email);
        }else{

            //create new resume
            resume = Resume.builder()
                    .extractedText(extractedText)
                    .candidateEmail(email)
                    .s3Key(S3key)
                    .fileName(file.getOriginalFilename())
                    .uploadedAt(LocalDateTime.now())
                    .build();
            log.info("New resume created for candidate: {}", email);
        }
        resumeRepository.save(resume);


        //step 4 : publish the kafka event
        ResumeUploadEvent resumeUploadEvent = new ResumeUploadEvent(
                resume.getCandidateEmail(),
                resume.getExtractedText(),
                resume.getS3Key());

        kafkaTemplate.send(RESUME_UPLOADED_TOPIC,email,resumeUploadEvent);
        log.info("ResumeUploadedEvent published for candidate : {}", email);

        return new ResumeResponse(
                resume.getId(),
                resume.getCandidateEmail(),
                resume.getFileName(),
                resume.getUploadedAt(),
                "Resume uploaded successfully"
        );
    }
    public void deleteFromS3(String s3Key){
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();
        s3Client.deleteObject(request);
        log.info("Old resume deleted from s3 : {}",s3Key);
    }

    private void uploadToS3(byte[]fileByte,String contentType, String s3key) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .key(s3key)
                .bucket(bucketName)
                .contentType(contentType)
                .contentLength((long)fileByte.length)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileByte));
    }

    private String extractTextPdf(byte[]file) throws IOException {
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    public ResumeResponse getMyResume(String email) {
        Resume resume = resumeRepository.findByCandidateEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Resume not found "));

        return new ResumeResponse(
                resume.getId(),
                resume.getCandidateEmail(),
                resume.getFileName(),
                resume.getUploadedAt(),
                "Resume fetched successfully");
    }
}

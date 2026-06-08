package com.jobboard.jobboardapplication.notification.consumer;

import com.jobboard.jobboardapplication.notification.service.NotificationService;
import com.jobboard.jobboardapplication.resume.event.ResumeUploadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumeUploadedConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "resume.uploaded",
            groupId = "resume-notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeResumeUploadedEvent(Map<String, Object> payload) {
        log.info("Received ResumeUploadedEvent: {}", payload);

        String candidateEmail = (String) payload.get("candidateEmail");
        String s3Key = (String) payload.get("s3Key");

        notificationService.notifyResumeUploaded(candidateEmail, s3Key);
    }
}

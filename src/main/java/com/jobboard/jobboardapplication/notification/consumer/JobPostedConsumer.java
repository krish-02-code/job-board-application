package com.jobboard.jobboardapplication.notification.consumer;

import com.jobboard.jobboardapplication.job.event.JobPostedEvent;
import com.jobboard.jobboardapplication.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobPostedConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "job.posted",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory")

    @KafkaListener(
            topics = "job.posted",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumerJobPostedEvent(Map<String, Object> payload) {
        log.info("Received JobPostedEvent: {}", payload);

        // manually map from HashMap
        Long jobId = payload.get("id") != null ?
                Long.valueOf(payload.get("id").toString()) : null;
        String title = (String) payload.get("title");
        String location = (String) payload.get("location");
        String requiredSkills = (String) payload.get("requiredSkills");

        notificationService.notifyNewJob(title, location, requiredSkills, jobId);
    }
}

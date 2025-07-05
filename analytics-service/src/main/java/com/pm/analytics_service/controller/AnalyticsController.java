package com.pm.analytics_service.controller;

import com.pm.analytics_service.kafka.KafkaConsumer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final KafkaConsumer kafkaConsumer;

    public AnalyticsController(KafkaConsumer kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("patientsAdded", kafkaConsumer.getAddedCount());
        status.put("patientsUpdated", kafkaConsumer.getUpdatedCount());
        return status; // Spring Boot auto-converts Map -> JSON
    }
}

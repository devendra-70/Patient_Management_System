package com.pm.patient_service.kafka;

import com.pm.patient_service.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${kafka.enabled:true}")
    private boolean kafkaEnabled;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient, PatientEvent.EventType eventType) {
        if (!kafkaEnabled) {
            log.info("Kafka is disabled. Skipping event publish for patientId: {}", patient.getId());
            return;
        }

        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType(eventType)  // ✅ enum, not string
                .build();

        try {
            kafkaTemplate.send("patient", event.toByteArray());
            log.info("{} event sent for patientId: {}", eventType, patient.getId());
        } catch (Exception e) {
            log.error("Error sending Patient event: {}", event, e);
            throw new RuntimeException(e);
        }
    }
}

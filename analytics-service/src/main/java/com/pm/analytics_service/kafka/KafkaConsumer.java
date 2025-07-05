package com.pm.analytics_service.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final AtomicInteger addedCount = new AtomicInteger(0);
    private final AtomicInteger updatedCount = new AtomicInteger(0);

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);

            switch (patientEvent.getEventType()) {
                case ADDED:
                    addedCount.incrementAndGet();
                    break;
                case UPDATED:
                    updatedCount.incrementAndGet();
                    break;
                default:
                    log.warn("Unknown event type: {}", patientEvent.getEventType());
                    break;
            }

            log.info("Received Patient Event: [Type={}, PatientId={}, Name={}, Email={}]",
                    patientEvent.getEventType(),
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail());

        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }


    public int getAddedCount() {
        return addedCount.get();
    }

    public int getUpdatedCount() {
        return updatedCount.get();
    }
}

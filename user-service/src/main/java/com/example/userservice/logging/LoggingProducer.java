package com.example.userservice.logging;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class LoggingProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendLog(String message, String messageType) {
        Map<String, Object> payload = Map.of(
                "message", message,
                "messageType", messageType,
                "dateTime", Instant.now().toString());

        try {
            String json = new ObjectMapper().writeValueAsString(payload);
            kafkaTemplate.send("logging-topic", json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
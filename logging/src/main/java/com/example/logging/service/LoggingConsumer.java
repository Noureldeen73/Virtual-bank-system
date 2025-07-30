package com.example.logging.service;

import com.example.logging.model.LogEntity;
import com.example.logging.dto.LogMessage;
import com.example.logging.repository.LogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class LoggingConsumer {

    @Autowired
    private LogRepository logRepository;

    @KafkaListener(topics = "logging-topic", groupId = "logging-group")
    public void consume(String rawMessage) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            LogMessage logMessage = mapper.readValue(rawMessage, LogMessage.class);

            LogEntity entity = new LogEntity();
            entity.setMessage(logMessage.getMessage());
            entity.setMessageType(logMessage.getMessageType());
            entity.setDateTime(Instant.parse(logMessage.getDateTime()));

            logRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

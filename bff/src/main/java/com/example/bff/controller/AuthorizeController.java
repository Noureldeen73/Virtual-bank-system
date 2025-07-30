package com.example.bff.controller;

import com.example.bff.Dto.UserDto;
import com.example.bff.logging.LoggingProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api")
public class AuthorizeController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoggingProducer loggingProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        logAsJson(userDto, "Request");

        if (userDto.getUsername() == null || userDto.getPassword() == null) {
            String error = "Missing required fields.";
            loggingProducer.sendLog("{\"error\": \"" + error + "\"}", "Response");
            return ResponseEntity.badRequest().body(error);
        }

        String url = "http://localhost:8080/users/login";
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, userDto, Object.class);
            logAsJson(response.getBody(), "Response");
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException ex) {
            loggingProducer.sendLog("{\"error\": \"" + ex.getMessage() + "\"}", "Response");
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        logAsJson(userDto, "Request");

        if (userDto.getUsername() == null || userDto.getFirstName() == null || userDto.getLastName() == null ||
                userDto.getPassword() == null || userDto.getEmail() == null) {
            String error = "Missing required fields.";
            loggingProducer.sendLog("{\"error\": \"" + error + "\"}", "Response");
            return ResponseEntity.badRequest().body(error);
        }

        String url = "http://localhost:8080/users/register";
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, userDto, Object.class);
            logAsJson(response.getBody(), "Response");
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException ex) {
            loggingProducer.sendLog("{\"error\": \"" + ex.getMessage() + "\"}", "Response");
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        }
    }

    private void logAsJson(Object obj, String type) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            loggingProducer.sendLog(json, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

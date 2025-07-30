package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.logging.LoggingProducer;
import com.example.userservice.model.Users;
import com.example.userservice.service.UserService;
import com.example.userservice.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserServiceController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoggingProducer loggingProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    public String home() {
        loggingProducer.sendLog("GET /users/", "Request");
        String message = "User Service is running.";
        loggingProducer.sendLog("{\"message\": \"" + message + "\"}", "Response");
        return message;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {
        logAsJson(user, "Request");

        try {
            userService.registerUser(user);
            ResponseEntity<?> response = ResponseHandler.createResponse(user, "User registered successfully", 201);
            logAsJson(response.getBody(), "Response");
            return response;
        } catch (Exception e) {
            ResponseEntity<?> error = ResponseHandler.createResponse(null, e.getMessage(), 409);
            logAsJson(error.getBody(), "Response");
            return error;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto credentials) {
        logAsJson(credentials, "Request");

        try {
            Users user = userService.AuthenticateUser(credentials.getUsername(), credentials.getPassword());
            ResponseEntity<?> response = ResponseHandler.loginResponse(user, "User logged successfully", 200);
            logAsJson(response.getBody(), "Response");
            return response;
        } catch (Exception e) {
            ResponseEntity<?> error = ResponseHandler.loginResponse(null, e.getMessage(), 401);
            logAsJson(error.getBody(), "Response");
            return error;
        }
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable("id") UUID id) {
        loggingProducer.sendLog("GET /users/" + id + "/profile", "Request");

        try {
            Users user = userService.getUserById(id);
            ResponseEntity<?> response = ResponseHandler.profileResponse(user, "", 200);
            logAsJson(response.getBody(), "Response");
            return response;
        } catch (Exception e) {
            ResponseEntity<?> error = ResponseHandler.profileResponse(null, "User with ID " + id + " not found", 404);
            logAsJson(error.getBody(), "Response");
            return error;
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

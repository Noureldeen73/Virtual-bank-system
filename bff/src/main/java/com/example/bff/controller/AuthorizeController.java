package com.example.bff.controller;

import com.example.bff.Dto.UserDto;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getPassword() == null) {
            return ResponseEntity.badRequest().body("Missing required fields.");
        }

        String url = "http://localhost:8080/users/login";
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, userDto, Object.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        // Only check for nulls to avoid errors (minimal backend validation)
        if (userDto.getUsername() == null || userDto.getFirstName() == null || userDto.getLastName() == null || userDto.getPassword() == null || userDto.getEmail() == null) {
            return ResponseEntity.badRequest().body("Missing required fields.");
        }

        String url = "http://localhost:8080/users/register";
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, userDto, Object.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        }
    }

}

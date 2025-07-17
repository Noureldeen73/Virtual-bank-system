package com.example.userservice.controller;


import com.example.userservice.model.Users;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserServiceController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "User Service is running.";
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user){
        try {
            userService.registerUser(user);
            Map<String,Object> response = new HashMap<>();
            response.put("userId:", user.getId());
            response.put("username:", user.getUsername());
            response.put("message:", "User registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "409");
            errorResponse.put("error", "Conflict");
            errorResponse.put("message", "Username or email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials){
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            Users user = userService.AuthenticateUser(username, password);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (AccessDeniedException | java.nio.file.AccessDeniedException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "401");
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable("id") UUID id) {
        try{
            Users user = userService.getUserById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirst_name());
            response.put("lastName", user.getLast_name());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "404");
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", "User with ID " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}

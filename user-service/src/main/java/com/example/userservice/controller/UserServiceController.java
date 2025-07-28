package com.example.userservice.controller;


import com.example.userservice.dto.UserDto;
import com.example.userservice.model.Users;
import com.example.userservice.service.UserService;
import com.example.userservice.util.ResponseHandler;
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
        System.out.println(user.toString());
        try {
            userService.registerUser(user);
            return ResponseHandler.createResponse(user, "User registered successfully", 201);
        } catch (Exception e) {
            return ResponseHandler.createResponse(null, e.getMessage().toString(), 409);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        try {
            Users user = userService.AuthenticateUser(username, password);
            return ResponseHandler.loginResponse(user, "User logged successfully", 200);
        } catch (Exception e) {
            return ResponseHandler.loginResponse(null, e.getMessage(), 401);
        }
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable("id") UUID id) {
        try{
            Users user = userService.getUserById(id);
            return ResponseHandler.profileResponse(user, "", 200);
        }catch (Exception e){
            return ResponseHandler.profileResponse(null, "User with ID " + id.toString() +  " not found", 404);
        }
    }
}

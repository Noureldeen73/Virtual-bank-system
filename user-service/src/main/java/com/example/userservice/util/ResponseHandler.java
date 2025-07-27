package com.example.userservice.util;

import com.example.userservice.model.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    public static ResponseEntity<?> createResponse(Users user, String message, String status) {
        if (user == null) {
            Map<String, Object> mp = new HashMap<>();
            mp.put("status", status);
            mp.put("error", "Conflict");
            mp.put("message", message);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mp);
        }
        Map<String, Object> mp = new HashMap<>();
        mp.put("userId", user.getId());
        mp.put("username", user.getUsername());
        mp.put("message", message);
        return ResponseEntity.status(HttpStatus.CREATED).body(mp);
    }

    public static ResponseEntity<?> loginResponse(Users user, String message, String status) {
        if( user == null) {
            Map<String, Object> mp = new HashMap<>();
            mp.put("status", status);
            mp.put("error", "Unauthorized");
            mp.put("message", message);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mp);
        }
        Map<String, Object> mp = new HashMap<>();
        mp.put("userId", user.getId());
        mp.put("username", user.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(mp);
    }
    public static ResponseEntity<?> profileResponse(Users user, String message, String status) {
        if (user == null) {
            Map<String, Object> mp = new HashMap<>();
            mp.put("error", "Not Found");
            mp.put("status", status);
            mp.put("message", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mp);
        }
        Map<String, Object> mp = new HashMap<>();
        mp.put("userId", user.getId());
        mp.put("username", user.getUsername());
        mp.put("email", user.getEmail());
        mp.put("firstName", user.getFirstName());
        mp.put("lastName", user.getLastName());
        return ResponseEntity.status(HttpStatus.OK).body(mp);
    }
}


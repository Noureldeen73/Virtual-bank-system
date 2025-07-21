package com.example.account.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String first_name;
    private String last_name;
}

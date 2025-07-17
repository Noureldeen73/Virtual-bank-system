package com.example.userservice.service;

import com.example.userservice.model.Users;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface UserService {
    void registerUser(Users user);
    Users AuthenticateUser(String username, String password) throws AccessDeniedException;
    Users getUserById(UUID id);
}

package com.example.userservice.dao;

import com.example.userservice.model.Users;

import java.util.Optional;
import java.util.UUID;

public interface UserDao {
    void createUser(Users user);
    Users getUserById(UUID id);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<Users> findByUsername(String username);

}

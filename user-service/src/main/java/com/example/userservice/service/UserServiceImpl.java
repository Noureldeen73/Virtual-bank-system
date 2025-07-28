package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public void registerUser(Users user) {
        try{
            ValidateUserInput(user);
        }catch(IllegalArgumentException e){
            throw e;
        }
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userDao.createUser(user);
    }

    @Override
    @Transactional
    public Users AuthenticateUser(String username, String password) throws AccessDeniedException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AccessDeniedException("Username and password cannot be null or blank");
        }
        Optional<Users> userOptional = userDao.findByUsername(username);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user;
            } else {
                throw new AccessDeniedException("Invalid credentials");
            }
        } else {
            throw new AccessDeniedException("Invalid credentials");
        }
    }

    @Override
    public Users getUserById(UUID id) {
        Users user = userDao.getUserById(id);
        if (user == null) {
            throw new IllegalStateException("User not found");
        }
        return user;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    private boolean noNullOrBlank(String str) {
        return str != null && !str.isBlank();
    }

    private void ValidateUserInput(Users user) {
        if(userDao.existsByUsername(user.getUsername())){
            throw new IllegalStateException("Username already exists");
        }
        if(userDao.existsByEmail(user.getEmail())){
            throw new IllegalStateException("Email already exists");
        }
        if(user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if(user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if(!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!noNullOrBlank(user.getUsername())) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (!noNullOrBlank(user.getFirstName())) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (!noNullOrBlank(user.getLastName())) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
        if (!noNullOrBlank(user.getEmail())) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }
}

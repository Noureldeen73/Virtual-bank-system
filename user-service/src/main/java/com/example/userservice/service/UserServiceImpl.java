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
        if(userDao.existsByUsername(user.getUsername())){
            throw new IllegalStateException("Username already exists");
        }
        if(userDao.existsByEmail(user.getEmail())){
            throw new IllegalStateException("Email already exists");
        }

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userDao.createUser(user);
    }

    @Override
    @Transactional
    public Users AuthenticateUser(String username, String password) throws AccessDeniedException {
        Optional<Users> userOptional = userDao.findByUsername(username);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user; // Authentication successful
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
}

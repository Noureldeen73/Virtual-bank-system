package com.example.userservice.dao;

import com.example.userservice.model.Users;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    EntityManager em;

    @Override
    public void createUser(Users user) {
        em.persist(user);
        em.flush();  // Ensure the user is saved immediately
    }

    @Override
    public Users getUserById(UUID id) {
        // Find the user by ID
        return em.find(Users.class, id);
    }

    @Override
    public Boolean existsByEmail(String email) {
        // Check if a user with the given email exists
        Long count = em.createQuery("SELECT COUNT(u) FROM Users u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Boolean existsByUsername(String username) {
        // Check if a user with the given username exists
        Long count = em.createQuery("SELECT COUNT(u) FROM Users u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<Users> findByUsername(String username) {
        // Find a user by username
        try {
            Users user = em.createQuery("SELECT u FROM Users u WHERE u.username = :username", Users.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (Exception e) {
            // If no user is found, return an empty Optional
            return Optional.empty();
        }
    }
}

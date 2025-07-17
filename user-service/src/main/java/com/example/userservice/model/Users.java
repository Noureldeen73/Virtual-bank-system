package com.example.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Users")
public class Users {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 50, message = "Username must be at least 3 and at most 50 characters long")
    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 255, message = "Password must be at least 6 characters long")
    @Column(length = 255, nullable = false)
    private String password_hash;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 50, message = "First name must be at least 1 and at most 50 characters long")
    @Column(length = 50, nullable = false)
    private String first_name;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 50, message = "Last name must be at least 1 and at most 50 characters long")
    @Column(length = 50, nullable = false)
    private String last_name;

    private LocalDateTime created_at = LocalDateTime.now();
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", created_at=" + created_at +
                '}';
    }

    public boolean isEmpty() {
        return username == null || username.isEmpty() ||
               email == null || email.isEmpty() ||
               password_hash == null || password_hash.isEmpty() ||
               first_name == null || first_name.isEmpty() ||
               last_name == null || last_name.isEmpty();
    }
}

package com.example.logging.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logging.model.LogEntity;

public interface LogRepository extends JpaRepository<LogEntity, UUID> {
}

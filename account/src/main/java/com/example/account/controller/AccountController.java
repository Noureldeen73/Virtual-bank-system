package com.example.account.controller;

import com.example.account.dto.AccountResponse;
import com.example.account.dto.CreateAccountRequest;
import com.example.account.dto.TransferRequest;
import com.example.account.logging.LoggingProducer;
import com.example.account.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class AccountController {

    @Autowired
    private AccountService service;

    @Autowired
    private LoggingProducer loggingProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PutMapping("/accounts/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest req) {
        logAsJson(req, "Request");
        try {
            service.transfer(req);
            Map<String, String> response = Map.of("message", "Account updated successfully.");
            logAsJson(response, "Response");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            logAsJson(error, "Response");
            return ResponseEntity.status(400).body(error);
        }
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> create(@RequestBody CreateAccountRequest req) {
        logAsJson(req, "Request");
        AccountResponse response = service.createAccount(req);
        Map<String, Object> result = Map.of(
                "accountId", response.getAccountId(),
                "accountNumber", response.getAccountNumber(),
                "message", "Account created successfully.");
        logAsJson(result, "Response");
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<?> get(@PathVariable UUID accountId) {
        loggingProducer.sendLog("GET /accounts/" + accountId, "Request");
        try {
            AccountResponse response = service.getAccount(accountId);
            logAsJson(response, "Response");
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, Object> error = Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "Account with ID " + accountId + " not found.");
            logAsJson(error, "Response");
            return ResponseEntity.status(404).body(error);
        }
    }

    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<?> getUserAccounts(@PathVariable UUID userId) {
        loggingProducer.sendLog("GET /users/" + userId + "/accounts", "Request");
        List<AccountResponse> accounts = service.getUserAccounts(userId);
        if (accounts.isEmpty()) {
            Map<String, Object> error = Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "No accounts found for user ID " + userId + ".");
            logAsJson(error, "Response");
            return ResponseEntity.status(404).body(error);
        }
        logAsJson(accounts, "Response");
        return ResponseEntity.ok(accounts);
    }

    // Helper method to convert any object to JSON and log it
    private void logAsJson(Object data, String type) {
        try {
            String json = objectMapper.writeValueAsString(data);
            loggingProducer.sendLog(json, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

package com.example.account.controller;

import com.example.account.dto.AccountResponse;
import com.example.account.dto.CreateAccountRequest;
import com.example.account.dto.TransferRequest;
import com.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class AccountController {

    @Autowired
    private AccountService service;

    @PutMapping("/accounts/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest req) {
        service.transfer(req);
        return ResponseEntity.ok(java.util.Map.of("message", "Account updated successfully."));
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> create(@RequestBody CreateAccountRequest req) {
        AccountResponse response = service.createAccount(req);
        return ResponseEntity.status(201).body(
                java.util.Map.of(
                        "accountId", response.getAccountId(),
                        "accountNumber", response.getAccountNumber(),
                        "message", "Account created successfully."));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<?> get(@PathVariable UUID accountId) {
        try {
            return ResponseEntity.ok(service.getAccount(accountId));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(
                    java.util.Map.of(
                            "status", 404,
                            "error", "Not Found",
                            "message", "Account with ID " + accountId + " not found."));
        }
    }

    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<?> getUserAccounts(@PathVariable UUID userId) {
        List<AccountResponse> accounts = service.getUserAccounts(userId);
        if (accounts.isEmpty()) {
            return ResponseEntity.status(404).body(
                    java.util.Map.of(
                            "status", 404,
                            "error", "Not Found",
                            "message", "No accounts found for user ID " + userId + "."));
        }
        return ResponseEntity.ok(accounts);
    }
}

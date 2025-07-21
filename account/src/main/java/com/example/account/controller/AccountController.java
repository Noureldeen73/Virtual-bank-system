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
    public ResponseEntity<String> transfer(@RequestBody TransferRequest req) {
        service.transfer(req);
        return ResponseEntity.ok("Transfer successful.");
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest req) {
        return ResponseEntity.status(201).body(service.createAccount(req));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountResponse> get(@PathVariable UUID accountId) {
        return ResponseEntity.ok(service.getAccount(accountId));
    }

    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<List<AccountResponse>> getUserAccounts(@PathVariable UUID userId) {
        List<AccountResponse> accounts = service.getUserAccounts(userId);
        if (accounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(accounts);
    }
}

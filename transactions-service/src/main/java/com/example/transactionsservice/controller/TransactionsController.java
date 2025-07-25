package com.example.transactionsservice.controller;

import com.example.transactionsservice.model.Transaction;
import com.example.transactionsservice.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class TransactionsController {

    @Autowired
    private TransactionsService transactionsService;

    @GetMapping("/account/{accountId}/transactions")
    public ResponseEntity<Map<Transaction, Character>> getTransactions(@PathVariable("accountId") UUID accountId) {
        Map<Transaction, Character> transactions = transactionsService.getTransactionsByAccountId(accountId);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(transactions);
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactions);

    }

    @PostMapping("/transactions/transfer/initiate")
    public ResponseEntity<?> initiateTransfer(@RequestBody Transaction transaction) {
        try {
            transaction = transactionsService.initiateTransaction(transaction);
            Map<String, Object> response = new HashMap<>();
            response.put("transactionsId", transaction.getId());
            response.put("Status", transaction.getStatus());
            response.put("timestamp", transaction.getCreated_at());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/transactions/transfer/execution")
    public ResponseEntity<?> executeTransfer(@RequestBody Transaction transaction) {
        try {
            transaction = transactionsService.executeTransaction(transaction);
            Map<String, Object> response = new HashMap<>();
            response.put("transactionsId", transaction.getId());
            response.put("Status", transaction.getStatus());
            response.put("timestamp", transaction.getCreated_at());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/transactions/latest")
    public ResponseEntity<Map<String, Object>> getLatestTransactionTimestamp(
            @RequestParam("accountId") UUID accountId) {
        Transaction latestTransaction = transactionsService.getLatestTransactionByAccountId(accountId);
        Map<String, Object> response = new HashMap<>();
        if (latestTransaction == null) {
            response.put("timestamp", null);
            return ResponseEntity.ok(response);
        }
        response.put("timestamp",
                latestTransaction.getCreated_at() != null ? latestTransaction.getCreated_at().toString() + "Z" : null);
        return ResponseEntity.ok(response);
    }
}

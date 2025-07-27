package com.example.transactionsservice.controller;

import com.example.transactionsservice.dto.TransactionDto;
import com.example.transactionsservice.model.Transaction;
import com.example.transactionsservice.service.TransactionsService;
import com.example.transactionsservice.util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class TransactionsController {

    @Autowired
    private TransactionsService transactionsService;

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable("accountId") UUID accountId) {
        List<Map<String, Object>> transactions = transactionsService.getTransactionsByAccountId(accountId);
        if (transactions.isEmpty()) {
            Map<String, String> emptyResponse = new HashMap<>();
            emptyResponse.put("message", "No transactions found for this account");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(emptyResponse);
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactions);

    }

    @PostMapping("/transactions/transfer/initiation")
    public ResponseEntity<?> initiateTransfer(@RequestBody TransactionDto transaction) {
        try {
            Transaction initiatedTransaction = transactionsService.initiateTransaction(transaction);
            return ResponseHandler.generateResponse(initiatedTransaction, "", "200");
        } catch (IllegalArgumentException e) {
            return ResponseHandler.generateResponse(null, e.getMessage().toString(), "400");
        }
    }

    @PostMapping("/transactions/transfer/execution")
    public ResponseEntity<?> executeTransfer(@RequestBody TransactionDto transaction) {
        try {
            Transaction executeTransaction = transactionsService.executeTransaction(transaction);
            return ResponseHandler.generateResponse(executeTransaction, "", "200");
        } catch (IllegalArgumentException e) {
            return ResponseHandler.generateResponse(null, e.getMessage().toString(), "400");
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

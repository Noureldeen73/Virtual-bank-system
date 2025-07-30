package com.example.transactionsservice.controller;

import com.example.transactionsservice.dto.TransactionDto;
import com.example.transactionsservice.logging.LoggingProducer;
import com.example.transactionsservice.model.Transaction;
import com.example.transactionsservice.service.TransactionsService;
import com.example.transactionsservice.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TransactionsController {

    @Autowired
    private TransactionsService transactionsService;

    @Autowired
    private LoggingProducer loggingProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable("accountId") UUID accountId) {
        loggingProducer.sendLog("GET /accounts/" + accountId + "/transactions", "Request");

        List<Map<String, Object>> transactions = transactionsService.getTransactionsByAccountId(accountId);

        if (transactions.isEmpty()) {
            Map<String, String> emptyResponse = Map.of("message", "No transactions found for this account");
            logAsJson(emptyResponse, "Response");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(emptyResponse);
        }

        logAsJson(transactions, "Response");
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    @PostMapping("/transactions/transfer/initiation")
    public ResponseEntity<?> initiateTransfer(@RequestBody TransactionDto transaction) {
        logAsJson(transaction, "Request");
        try {
            Transaction initiatedTransaction = transactionsService.initiateTransaction(transaction);
            ResponseEntity<?> response = ResponseHandler.generateResponse(initiatedTransaction, "", "200");
            logAsJson(response.getBody(), "Response");
            return response;
        } catch (IllegalArgumentException e) {
            ResponseEntity<?> error = ResponseHandler.generateResponse(null, e.getMessage(), "400");
            logAsJson(error.getBody(), "Response");
            return error;
        }
    }

    @PostMapping("/transactions/transfer/execution")
    public ResponseEntity<?> executeTransfer(@RequestBody TransactionDto transaction) {
        logAsJson(transaction, "Request");
        try {
            Transaction executeTransaction = transactionsService.executeTransaction(transaction);
            ResponseEntity<?> response = ResponseHandler.generateResponse(executeTransaction, "", "200");
            logAsJson(response.getBody(), "Response");
            return response;
        } catch (IllegalArgumentException e) {
            ResponseEntity<?> error = ResponseHandler.generateResponse(null, e.getMessage(), "400");
            logAsJson(error.getBody(), "Response");
            return error;
        }
    }

    @GetMapping("/transactions/latest")
    public ResponseEntity<Map<String, Object>> getLatestTransactionTimestamp(
            @RequestParam("accountId") UUID accountId) {
        loggingProducer.sendLog("GET /transactions/latest?accountId=" + accountId, "Request");

        Transaction latestTransaction = transactionsService.getLatestTransactionByAccountId(accountId);
        Map<String, Object> response = new HashMap<>();

        if (latestTransaction == null) {
            response.put("timestamp", null);
        } else {
            response.put("timestamp", latestTransaction.getCreated_at() + "Z");
        }

        logAsJson(response, "Response");
        return ResponseEntity.ok(response);
    }

    // 🔁 Helper to convert any object to JSON and send to Kafka
    private void logAsJson(Object obj, String type) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            loggingProducer.sendLog(json, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

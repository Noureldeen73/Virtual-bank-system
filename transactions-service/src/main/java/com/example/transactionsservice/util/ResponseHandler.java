package com.example.transactionsservice.util;

import com.example.transactionsservice.model.Transaction;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseHandler {

    public static ResponseEntity<?> generateResponse(Transaction transaction, String message, String status) {
        if (transaction == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", status,
                    "error", "Not Found",
                    "message", message
            ));
        }
        return ResponseEntity.status(200).body(Map.of(
                "transactionId", transaction.getId(),
                "status", transaction.getStatus(),
                "timestamp", transaction.getCreated_at()
        ));
    }
}

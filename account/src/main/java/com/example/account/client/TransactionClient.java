package com.example.account.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class TransactionClient {
    @Autowired
    private RestTemplate restTemplate;

    private final String TRANSACTION_SERVICE_BASE_URL = "http://localhost:8082";

    public Instant getLatestTransactionTimestamp(UUID accountId) {
        String url = TRANSACTION_SERVICE_BASE_URL + "/transactions/latest?accountId=" + accountId;
        Map<?, ?> response = restTemplate.getForObject(url, Map.class);
        if (response != null && response.get("timestamp") != null) {
            return Instant.parse(response.get("timestamp").toString());
        }
        // If no transaction found, return null
        return null;
    }
}
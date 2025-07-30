package com.example.bff.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.bff.Dto.TransactionsDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.bff.logging.LoggingProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class TransferController {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private LoggingProducer loggingProducer;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @PostMapping("/transfer")
  public ResponseEntity<?> transfer(@RequestBody TransactionsDto transferDto) {
    String url = "http://localhost:8081/accounts/transfer";
    try {
      if (transferDto.getFromAccountId() == null ||
          transferDto.getToAccountId() == null ||
          transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        return ResponseEntity.badRequest().body("Invalid transfer data.");
      }

      // 🔹 Log the request
      logAsJson(transferDto, "Request");

      ResponseEntity<?> response = restTemplate.exchange(
          url,
          org.springframework.http.HttpMethod.PUT,
          new org.springframework.http.HttpEntity<>(transferDto),
          Object.class);

      // 🔹 Log the successful response
      logAsJson(response.getBody(), "Response");

      return ResponseEntity.ok(response.getBody());

    } catch (Exception e) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("status", "500");
      errorResponse.put("error", "Internal Server Error");
      errorResponse.put("message", "Failed to process transfer: " + e.getMessage());

      // 🔹 Log the error response
      logAsJson(errorResponse, "Response");

      return ResponseEntity.status(500).body(errorResponse);
    }
  }

  private void logAsJson(Object data, String type) {
    try {
      String json = objectMapper.writeValueAsString(data);
      loggingProducer.sendLog(json, type);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}

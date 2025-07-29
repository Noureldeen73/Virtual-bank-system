package com.example.bff.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.bff.Dto.TransactionsDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class TransferController {

  @Autowired
  private RestTemplate restTemplate;

  @PostMapping("/transfer")
  public ResponseEntity<?> transfer(@RequestBody TransactionsDto transferDto) {
    String url = "http://localhost:8081/accounts/transfer";
    try {
      if( transferDto.getFromAccountId() == null || 
          transferDto.getToAccountId() == null || 
          transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        return ResponseEntity.badRequest().body("Invalid transfer data.");
      }
      ResponseEntity<?> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.PUT, new org.springframework.http.HttpEntity<>(transferDto), Object.class);
      return ResponseEntity.ok(response.getBody());
    }catch (Exception e) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("status", "500");
      errorResponse.put("error", "Internal Server Error");
      errorResponse.put("message", "Failed to process transfer: " + e.getMessage());
      return ResponseEntity.status(500).body(errorResponse);
    }
  }
  
}

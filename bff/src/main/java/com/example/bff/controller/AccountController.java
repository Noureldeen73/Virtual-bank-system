package com.example.bff.controller;

import com.example.bff.Dto.AccountDto;
import com.example.bff.logging.LoggingProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private LoggingProducer loggingProducer;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @PostMapping("/create")
  public ResponseEntity<?> createAccount(@RequestBody AccountDto accountDto) {
    logAsJson(accountDto, "Request");

    if (accountDto.getAccountType() == null || accountDto.getInitialBalance() == null) {
      String errorMsg = "Missing required fields.";
      loggingProducer.sendLog("{\"message\": \"" + errorMsg + "\"}", "Response");
      return ResponseEntity.badRequest().body(errorMsg);
    }

    String url = "http://localhost:8081/accounts";
    try {
      ResponseEntity<?> response = restTemplate.postForEntity(url, accountDto, Object.class);
      logAsJson(response.getBody(), "Response");
      return ResponseEntity.ok(response.getBody());
    } catch (HttpStatusCodeException ex) {
      String error = "Error creating account: " + ex.getResponseBodyAsString();
      loggingProducer.sendLog("{\"error\": \"" + error + "\"}", "Response");
      return ResponseEntity.status(ex.getStatusCode()).body(error);
    }
  }

  private void logAsJson(Object obj, String type) {
    try {
      String json = objectMapper.writeValueAsString(obj);
      loggingProducer.sendLog(json, type);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}

package com.example.bff.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.bff.logging.LoggingProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/bff")
public class DashboardController {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private LoggingProducer loggingProducer;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @GetMapping("/dashboard/{userId}")
  public ResponseEntity<?> getDashboard(@PathVariable UUID userId) {
    String UserUrl = "http://localhost:8080/users/" + userId + "/profile";
    String AccountsUrl = "http://localhost:8081/users/" + userId + "/accounts";
    List<Map<String, Object>> accountsResponse;
    try {
      // Log the incoming request
      loggingProducer.sendLog("{\"userId\": \"" + userId + "\"}", "Request");

      Map<String, Object> userResponse = restTemplate.getForObject(UserUrl, Map.class);
      try{
        accountsResponse = restTemplate.getForObject(AccountsUrl, List.class);

        for (Map<String, Object> account : accountsResponse) {
          UUID accountId = UUID.fromString(account.get("accountId").toString());
          String transactionsUrl = "http://localhost:8082/accounts/" + accountId + "/transactions";
          List<Map<String, Object>> transactionsResponse;
          try{
            transactionsResponse = restTemplate.getForObject(transactionsUrl, List.class);
          }catch (Exception e) {
            transactionsResponse = List.of();
          }
          account.put("transactions", transactionsResponse);

        }
      }catch(Exception e){
        accountsResponse = List.of();
      }

      Map<String, Object> response = new LinkedHashMap<>();
      response.put("username", userResponse.get("username"));
      response.put("firstName", userResponse.get("firstName"));
      response.put("lastName", userResponse.get("lastName"));
      response.put("email", userResponse.get("email"));
      response.put("accounts", accountsResponse);

      // Log the outgoing response
      logAsJson(response, "Response");

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("status", "500");
      errorResponse.put("error", "Internal Server Error");
      errorResponse.put("message", "Failed to fetch dashboard data: " + e.getMessage());

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

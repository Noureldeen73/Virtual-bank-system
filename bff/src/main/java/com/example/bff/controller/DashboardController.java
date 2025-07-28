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
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/bff")
public class DashboardController {

  @Autowired
  private RestTemplate restTemplate;

  @GetMapping("/dashboard/{userId}")
  public ResponseEntity<?> getMethodName(@PathVariable UUID userId) {
    String UserUrl = "http://localhost:8080/users/" + userId + "/profile";
    String AccountsUrl = "http://localhost:8081/users/" + userId + "/accounts";

    try{
      Map<String, Object> userResponse = restTemplate.getForObject(UserUrl, Map.class);
      List<Map<String, Object>> accountsResponse = restTemplate.getForObject(AccountsUrl, List.class);
  
      for (Map<String, Object> account : accountsResponse) {
        UUID accountId = UUID.fromString(account.get("accountId").toString());
        String transactionsUrl = "http://localhost:8082/accounts/" + accountId + "/transactions";
        List<Map<String, Object>> transactionsResponse;
        try{
          transactionsResponse = restTemplate.getForObject(transactionsUrl, List.class);
        }catch (Exception e) {
          // Handle case where no transactions exist or an error occurs
          transactionsResponse = List.of(); // Empty list if no transactions
        }
        account.put("transactions", transactionsResponse);
        
      }
      Map<String, Object>  response = new LinkedHashMap<>();
      response.put("username", userResponse.get("username"));
      response.put("firstName", userResponse.get("firstName"));
      response.put("lastName", userResponse.get("lastName"));
      response.put("email", userResponse.get("email"));
      response.put("accounts", accountsResponse);
      return ResponseEntity.ok(response);
    }catch(Exception e) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("status", "500");
      errorResponse.put("error", "Internal Server Error");
      errorResponse.put("message", "Failed to fetch dashboard data: " + e.getMessage());
      return ResponseEntity.status(500).body(errorResponse);
    } 
  }
  
}

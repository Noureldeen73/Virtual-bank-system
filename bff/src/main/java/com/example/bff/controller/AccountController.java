package com.example.bff.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.bff.Dto.AccountDto;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  @Autowired
  private RestTemplate restTemplate;

  @PostMapping("/create")
  public ResponseEntity<?> createAccount(@RequestBody AccountDto accountDto) {
    if (accountDto.getAccountType() == null || accountDto.getInitialBalance() == null) {
      
      return ResponseEntity.badRequest().body("Missing required fields.");
    }
    String url = "http://localhost:8081/accounts";
    try {
      ResponseEntity<?> response = restTemplate.postForEntity(url, accountDto, Object.class);
      return ResponseEntity.ok(response.getBody());
    } catch (HttpStatusCodeException ex) {
      return ResponseEntity.status(ex.getStatusCode()).body("Error creating account: " + ex.getResponseBodyAsString());
    }
  }
  

}

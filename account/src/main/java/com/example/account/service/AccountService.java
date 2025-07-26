package com.example.account.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.account.dto.AccountResponse;
import com.example.account.dto.CreateAccountRequest;
import com.example.account.dto.TransferRequest;
import com.example.account.dto.UserResponse;
import com.example.account.model.Account;
import com.example.account.model.AccountStatus;
import com.example.account.model.AccountType;
import com.example.account.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    private final String USER_SERVICE_BASE_URL = "http://localhost:8080/users";

    public UserResponse fetchUserDetails(UUID userId) {
        String url = USER_SERVICE_BASE_URL + "/" + userId + "/profile";
        return restTemplate.getForObject(url, UserResponse.class);
    }

    public List<AccountResponse> getUserAccounts(UUID userId) {
        // Fetch user to confirm existence
        UserResponse user = fetchUserDetails(userId);
        if (user == null) {
            throw new RuntimeException("User not found in user service");
        }

        // Proceed to fetch accounts
        return repository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        if (request.getInitialBalance() == null || request.getInitialBalance().signum() < 0) {
            throw new IllegalArgumentException("Invalid account type or initial balance.");
        }
        AccountType type;
        try {
            type = AccountType.valueOf(request.getAccountType());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid account type or initial balance.");
        }
        Account account = Account.builder()
                .accountNumber(UUID.randomUUID().toString().substring(0, 10))
                .accountType(type)
                .balance(request.getInitialBalance())
                .status(AccountStatus.ACTIVE)
                .userId(request.getUserId())
                .build();

        repository.save(account);
        return mapToResponse(account);
    }

    public AccountResponse getAccount(UUID accountId) {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToResponse(account);
    }

    public void transfer(TransferRequest req) {
        Account from = repository.findById(req.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("From account not found"));

        Account to = repository.findById(req.getToAccountId())
                .orElseThrow(() -> new RuntimeException("To account not found"));

        ResponseEntity<?> initiationResponse = restTemplate.postForEntity(
                "http://localhost:8082/transactions/transfer/initiation",
                req, Object.class);
        if (initiationResponse.getStatusCode().isError()) {
            throw new RuntimeException(initiationResponse.getBody().toString());
        }
        ResponseEntity<?> executionResponse = restTemplate.postForEntity(
                "http://localhost:8082/transactions/transfer/execution",
                req, Object.class);
        if (executionResponse.getStatusCode().isError()) {
            throw new RuntimeException(executionResponse.getBody().toString());
        }
        from.setBalance(from.getBalance().subtract(req.getAmount()));
        to.setBalance(to.getBalance().add(req.getAmount()));
        repository.save(from);
        repository.save(to);
    }

    private AccountResponse mapToResponse(Account acc) {
        return new AccountResponse(
                acc.getAccountId(),
                acc.getAccountNumber(),
                acc.getAccountType().name(),
                acc.getBalance(),
                acc.getStatus().name());
    }
}

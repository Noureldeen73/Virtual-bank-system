package com.example.account.jobs;

import com.example.account.client.TransactionClient;
import com.example.account.model.Account;
import com.example.account.model.AccountStatus;
import com.example.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class InactivateStaleAccountsJob {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionClient transactionClient;

    // Runs every hour
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void inactivateStaleAccounts() {
        List<Account> activeAccounts = accountRepository.findAll().stream()
                .filter(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                .toList();
        for (Account account : activeAccounts) {
            Instant lastTx = transactionClient.getLatestTransactionTimestamp(account.getAccountId());
            if (lastTx == null || Duration.between(lastTx, Instant.now()).toHours() > 24) {
                account.setStatus(AccountStatus.INACTIVE);
                accountRepository.save(account);
            }
        }
    }
}

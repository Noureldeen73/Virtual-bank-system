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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class InactivateStaleAccountsJob {
    private static final Logger logger = LoggerFactory.getLogger(InactivateStaleAccountsJob.class);
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionClient transactionClient;

    @Scheduled(fixedRate = 60 * 60 * 1000) // every 1 hour

    public void inactivateStaleAccounts() {
        logger.info("InactivateStaleAccountsJob is running...");
        List<Account> activeAccounts = accountRepository.findAll().stream()
                .filter(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                .toList();
        for (Account account : activeAccounts) {
            Instant lastTx = transactionClient.getLatestTransactionTimestamp(account.getAccountId());
            Instant now = java.time.ZonedDateTime.now().toInstant();
            System.out.println("Last transaction: " + lastTx);
            System.out.println("Current time: " + now);
            System.out.println("Duration: " + Duration.between(now, lastTx).toSeconds());
            if (lastTx == null || Duration.between(now, lastTx).toHours() > 24) {
                account.setStatus(AccountStatus.INACTIVE);
                accountRepository.save(account);
            }
        }
    }
}

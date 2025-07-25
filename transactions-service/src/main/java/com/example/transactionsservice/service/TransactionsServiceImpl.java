package com.example.transactionsservice.service;

import com.example.transactionsservice.dao.TransactionsDao;
import com.example.transactionsservice.dto.AccountDto;
import com.example.transactionsservice.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionsServiceImpl implements TransactionsService {

    @Autowired
    private TransactionsDao transactionsDao;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Transaction initiateTransaction(Transaction transaction) {
        if (transaction.getTo_account_id() == null || transaction.getFrom_account_id() == null) {
            throw new IllegalArgumentException("Invalid accounts or insufficient amount");
        }
        AccountDto fromAccount = restTemplate
                .getForObject("http://localhost:8081/accounts/" + transaction.getFrom_account_id(), AccountDto.class);
        AccountDto toAccount = restTemplate
                .getForObject("http://localhost:8081/accounts/" + transaction.getTo_account_id(), AccountDto.class);
        if (!validateTransactionAccounts(transaction, fromAccount, toAccount)) {
            throw new IllegalArgumentException("Invalid accounts or insufficient amount");
        }
        transactionsDao.createTransaction(transaction);
        if (!validateTransactionAmount(transaction, fromAccount)) {
            UUID transactionId = transaction.getId();
            transactionSuccess(transactionId);
            throw new IllegalArgumentException("Invalid accounts or insufficient amount");
        }
        return transaction;
    }

    @Override
    public Transaction executeTransaction(Transaction transaction) {
        if (transaction.getTo_account_id() == null || transaction.getFrom_account_id() == null) {
            throw new IllegalArgumentException("Invalid accounts or insufficient amount");
        }
        AccountDto fromAccount = restTemplate
                .getForObject("http://localhost:8081/accounts/" + transaction.getFrom_account_id(), AccountDto.class);
        AccountDto toAccount = restTemplate
                .getForObject("http://localhost:8081/accounts/" + transaction.getTo_account_id(), AccountDto.class);
        if (!validateTransactionAccounts(transaction, fromAccount, toAccount)) {
            throw new IllegalArgumentException("Invalid accounts or insufficient amount");
        }
        transactionsDao.createTransaction(transaction);
        if (!validateTransactionAmount(transaction, fromAccount)) {
            UUID transactionId = transaction.getId();
            transactionSuccess(transactionId);
            throw new IllegalArgumentException("Invalid accounts or insufficient amount");
        }
        restTemplate.postForObject("http://localhost:8081/accounts/" + transaction.getFrom_account_id() + "/withdraw",
                transaction.getAmount(), Void.class);
        restTemplate.postForObject("http://localhost:8081/accounts/" + transaction.getTo_account_id() + "/deposit",
                transaction.getAmount(), Void.class);
        UUID transactionId = transaction.getId();
        transactionSuccess(transactionId);
        return transaction;
    }

    @Override
    public void transactionSuccess(UUID transactionId) {
        transactionsDao.TransactionSuccess(transactionId);
    }

    @Override
    public void transactionFailed(UUID transactionId) {
        transactionsDao.TransactionFailed(transactionId);
    }

    @Override
    public Map<Transaction, Character> getTransactionsByAccountId(UUID accountId) {
        List<Transaction> transactions = transactionsDao.getTransactionsByAccountId(accountId);
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("No transactions found for the given account ID");
        }
        Map<Transaction, Character> map = new HashMap<>();
        transactions.forEach(transaction -> {
            if (transaction.getTo_account_id().equals(accountId)) {
                map.put(transaction, '+');
            } else if (transaction.getFrom_account_id().equals(accountId)) {
                map.put(transaction, '-');
            }
        });
        return map;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionsDao.getAllTransactions();
    }

    @Override
    public Transaction getTransactionById(UUID transactionId) {
        return transactionsDao.getTransactionById(transactionId);
    }

    @Override
    public Transaction getLatestTransactionByAccountId(UUID accountId) {
        return transactionsDao.getLatestTransactionByAccountId(accountId);
    }

    private Boolean validateTransactionAccounts(Transaction transaction, AccountDto fromAccount, AccountDto toAccount) {
        if (transaction.getTo_account_id() == null || transaction.getFrom_account_id() == null) {
            return false;
        }
        if (transaction.getFrom_account_id().equals(transaction.getTo_account_id())) {
            return false;
        }
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (fromAccount == null || toAccount == null) {
            return false;
        }
        return true;
    }

    private Boolean validateTransactionAmount(Transaction transaction, AccountDto fromAccount) {
        if (fromAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
            return false;
        }
        return true;
    }
}

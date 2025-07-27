package com.example.transactionsservice.service;

import com.example.transactionsservice.dao.TransactionsDao;
import com.example.transactionsservice.dto.AccountDto;
import com.example.transactionsservice.dto.TransactionDto;
import com.example.transactionsservice.model.Transaction;
import com.example.transactionsservice.model.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(noRollbackFor = IllegalArgumentException.class)
public class TransactionsServiceImpl implements TransactionsService {

    @Autowired
    private TransactionsDao transactionsDao;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Transaction initiateTransaction(TransactionDto transaction) {
        if (transaction.getFromAccountId() == null || transaction.getToAccountId() == null) {
            throw new IllegalArgumentException("One or both account IDs are invalid");
        }
        AccountDto fromAccount = restTemplate
                .getForObject("http://localhost:8081/accounts/" + transaction.getFromAccountId(), AccountDto.class);
        AccountDto toAccount = restTemplate
                .getForObject("http://localhost:8081/accounts/" + transaction.getToAccountId(), AccountDto.class);
        if (!validateTransactionAccounts(transaction, fromAccount, toAccount)) {
            throw new IllegalArgumentException("Invalid accounts or insufficient amount");
        }
        Transaction initiateTransaction = new Transaction();
        initiateTransaction.setFrom_account_id(transaction.getFromAccountId());
        initiateTransaction.setTo_account_id(transaction.getToAccountId());
        initiateTransaction.setAmount(transaction.getAmount());
        initiateTransaction.setDescription(transaction.getDescription());
        transactionsDao.createTransaction(initiateTransaction);
        if (!validateTransactionAmount(initiateTransaction.getAmount(), fromAccount)) {
            UUID transactionId = initiateTransaction.getId();
            System.out.println(transactionId);
            transactionFailed(transactionId);
            throw new IllegalArgumentException("Invalid accounts or insufficient amount");
        }
        return initiateTransaction;
    }

    @Override
    public Transaction executeTransaction(TransactionDto transaction) {
        Transaction initiatedTransaction = transactionsDao.getTransactionById(transaction.getTransactionId());
        if (initiatedTransaction == null) {
            throw new IllegalArgumentException("Transaction not found");
        }
        AccountDto fromAccount = restTemplate
                .getForObject("http://localhost:8081/accounts/" + initiatedTransaction.getFrom_account_id(),
                        AccountDto.class);
        if (!validateTransactionAmount(initiatedTransaction.getAmount(), fromAccount)) {
            UUID transactionId = transaction.getTransactionId();
            transactionFailed(transactionId);
            throw new IllegalArgumentException("Invalid accounts or insufficient amount");
        }
        transactionSuccess(initiatedTransaction.getId());
        return initiatedTransaction;
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
    public List<Map<String, Object>> getTransactionsByAccountId(UUID accountId) {
        List<Transaction> transactions = transactionsDao.getTransactionsByAccountId(accountId);
        List<Map<String, Object>> transactionList = transactions.stream()
                .map(transaction -> createTransactionMap(transaction, accountId))
                .toList();
        return transactionList;
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

    private Boolean validateTransactionAccounts(TransactionDto transaction, AccountDto fromAccount,
                                                AccountDto toAccount) {
        if (transaction.getToAccountId() == null || transaction.getFromAccountId() == null) {
            return false;
        }
        if (transaction.getFromAccountId().equals(transaction.getToAccountId())) {
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

    private Boolean validateTransactionAmount(BigDecimal amount, AccountDto fromAccount) {
        System.out.println("Validating transaction amount: " + amount + " for account: "
                + fromAccount.getAccountId());
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            return false;
        }
        return true;
    }

    private Map<String, Object> createTransactionMap(Transaction transaction, UUID accountId) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", transaction.getId());
        map.put("fromAccountId", transaction.getFrom_account_id());
        map.put("toAccountId", transaction.getTo_account_id());
        if (transaction.getTo_account_id().equals(accountId)
                && transaction.getStatus() == TransactionStatus.SUCCESS) {
            map.put("amount", "+" + transaction.getAmount());
            map.put("status", "SUCCESS");
        } else if (transaction.getFrom_account_id().equals(accountId)
                && transaction.getStatus() == TransactionStatus.SUCCESS) {
            map.put("amount", "-" + transaction.getAmount());
            map.put("status", "SUCCESS");
        } else if (transaction.getFrom_account_id().equals(accountId)
                && transaction.getStatus() == TransactionStatus.FAILED) {
            map.put("amount", "-" + transaction.getAmount());
            map.put("status", "FAILED");
        }
        if (transaction.getDescription() != null && !transaction.getDescription().isEmpty()) {
            map.put("description", transaction.getDescription());
        }
        return map;
    }

}

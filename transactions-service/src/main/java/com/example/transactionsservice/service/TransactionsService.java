package com.example.transactionsservice.service;

import com.example.transactionsservice.dto.TransactionDto;
import com.example.transactionsservice.model.Transaction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TransactionsService {

    Transaction initiateTransaction(TransactionDto transaction);

    Transaction executeTransaction(TransactionDto transaction);

    void transactionSuccess(UUID transactionId);

    void transactionFailed(UUID transactionId);

    List<Map<String, Object>> getTransactionsByAccountId(UUID accountId);

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(UUID transactionId);

    Transaction getLatestTransactionByAccountId(UUID accountId);
}

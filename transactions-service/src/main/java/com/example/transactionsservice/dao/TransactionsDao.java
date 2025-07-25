package com.example.transactionsservice.dao;

import com.example.transactionsservice.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionsDao {

    void createTransaction(Transaction transaction);

    void TransactionSuccess(UUID transactionId);

    void TransactionFailed(UUID transactionId);

    List<Transaction> getTransactionsByAccountId(UUID accountId);

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(UUID transactionId);

    Transaction getLatestTransactionByAccountId(UUID accountId);
}

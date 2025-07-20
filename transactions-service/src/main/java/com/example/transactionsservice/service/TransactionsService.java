package com.example.transactionsservice.service;

import com.example.transactionsservice.model.Transaction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TransactionsService {

    Transaction initiateTransaction(Transaction transaction);

    Transaction executeTransaction(Transaction transaction);

    void transactionSuccess(UUID transactionId);

    void transactionFailed(UUID transactionId);

    Map<Transaction, Character> getTransactionsByAccountId(UUID accountId);

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(UUID transactionId);

}

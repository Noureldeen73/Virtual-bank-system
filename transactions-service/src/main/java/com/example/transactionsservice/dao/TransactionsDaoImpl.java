package com.example.transactionsservice.dao;

import com.example.transactionsservice.model.Transaction;
import com.example.transactionsservice.model.TransactionStatus;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TransactionsDaoImpl implements TransactionsDao {

    @Autowired
    EntityManager entityManager;

    @Override
    public void createTransaction(Transaction transaction) {
        entityManager.persist(transaction);
        entityManager.flush();

    }

    @Override
    public void TransactionSuccess(UUID transactionId) {
        Transaction transaction = entityManager.find(Transaction.class, transactionId);
        if (transaction != null) {
            transaction.setStatus(TransactionStatus.SUCCESS);
            entityManager.merge(transaction);
        }
    }

    @Override
    public void TransactionFailed(UUID transactionId) {
        Transaction transaction = entityManager.find(Transaction.class, transactionId);
        if (transaction != null) {
            transaction.setStatus(TransactionStatus.FAILED);
            entityManager.merge(transaction);
        }
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(UUID accountId) {
        return entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.from_account_id = :accountId or t.to_account_id = :accountId",
                Transaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return entityManager.createQuery("select t FROM Transaction t", Transaction.class).getResultList();
    }

    @Override
    public Transaction getTransactionById(UUID transactionId) {
        return entityManager.find(Transaction.class, transactionId);
    }

    @Override
    public Transaction getLatestTransactionByAccountId(UUID accountId) {
        List<Transaction> transactions = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.from_account_id = :accountId or t.to_account_id = :accountId ORDER BY t.created_at DESC",
                Transaction.class)
                .setParameter("accountId", accountId)
                .setMaxResults(1)
                .getResultList();
        return transactions.isEmpty() ? null : transactions.get(0);
    }
}

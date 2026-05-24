package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.transaction.CreateTransactionRequest;
import com.rudra.financemanager.dto.transaction.TransactionResponse;
import com.rudra.financemanager.dto.transaction.UpdateTransactionRequest;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionResponse create(CreateTransactionRequest createTransactionRequest);
    List<TransactionResponse> getAll(LocalDate startDate, LocalDate endDate, Long categoryId);
    TransactionResponse update(Long id, UpdateTransactionRequest updateTransactionRequest);
    void delete(Long id);
}

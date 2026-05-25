package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.transaction.CreateTransactionRequest;
import com.rudra.financemanager.dto.transaction.TransactionResponse;
import com.rudra.financemanager.dto.transaction.UpdateTransactionRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing user transaction operations.
 */
public interface TransactionService {

    /**
     * Creates a new financial transaction for the current user.
     *
     * @param createTransactionRequest DTO containing parameters for the transaction.
     * @return TransactionResponse detailed DTO.
     */
    TransactionResponse create(CreateTransactionRequest createTransactionRequest);

    /**
     * Retrieves all transactions belonging to the current user, optionally filtered by date range and category.
     *
     * @param startDate  Optional filter to show transactions on or after this date.
     * @param endDate    Optional filter to show transactions on or before this date.
     * @param categoryId Optional filter to show transactions matching this category ID.
     * @return List of TransactionResponse DTOs.
     */
    List<TransactionResponse> getAll(LocalDate startDate, LocalDate endDate, Long categoryId);

    /**
     * Updates an existing transaction's amount, description, and/or category.
     *
     * @param id                       The identifier of the transaction to update.
     * @param updateTransactionRequest DTO containing updated transaction fields.
     * @return TransactionResponse detailed DTO.
     */
    TransactionResponse update(Long id, UpdateTransactionRequest updateTransactionRequest);

    /**
     * Deletes a transaction by its unique ID.
     *
     * @param id The identifier of the transaction to delete.
     */
    void delete(Long id);
}

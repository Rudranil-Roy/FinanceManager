package com.rudra.financemanager.controllers;

import com.rudra.financemanager.dto.transaction.CreateTransactionRequest;
import com.rudra.financemanager.dto.transaction.TransactionResponse;
import com.rudra.financemanager.dto.transaction.UpdateTransactionRequest;
import com.rudra.financemanager.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing financial transactions.
 * Provides endpoints for creating, retrieving with optional filters, updating, and deleting transactions.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    /**
     * Creates a new financial transaction (INCOME or EXPENSE) for the authenticated user.
     *
     * @param request DTO containing transaction details (amount, description, date, type, categoryId).
     * @return ResponseEntity with the created transaction's details and HTTP 201 status.
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all transactions belonging to the authenticated user, with optional filters.
     *
     * @param startDate  Optional filter to show transactions on or after this date.
     * @param endDate    Optional filter to show transactions on or before this date.
     * @param categoryId Optional filter to show transactions belonging to a specific category ID.
     * @return ResponseEntity containing a list of matching transactions.
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long categoryId
    ) {
        List<TransactionResponse> response = transactionService.getAll(startDate, endDate, categoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates details of an existing transaction by its ID (e.g. amount, description, category).
     * Date and Type of transaction are immutable.
     *
     * @param id      The ID of the transaction to update.
     * @param request DTO containing the updated fields.
     * @return ResponseEntity with the updated transaction details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateTransactionRequest request) {
        TransactionResponse response = transactionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a transaction by its ID.
     *
     * @param id The ID of the transaction to delete.
     * @return ResponseEntity with a 200 OK status on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok().build();
    }
}

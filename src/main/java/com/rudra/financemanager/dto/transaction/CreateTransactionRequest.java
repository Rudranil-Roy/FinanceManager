package com.rudra.financemanager.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object containing parameters to create a new transaction.
 * Holds transaction amount, date, category name, and an optional description.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTransactionRequest {

    /**
     * The transaction amount. Must be positive.
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be a positive value")
    private BigDecimal amount;

    /**
     * The date of the transaction. Must not be in the future.
     */
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    /**
     * The name of the category this transaction belongs to.
     */
    @NotBlank(message = "Category is required")
    private String category;

    /**
     * Optional description or notes for the transaction.
     */
    private String description;
}

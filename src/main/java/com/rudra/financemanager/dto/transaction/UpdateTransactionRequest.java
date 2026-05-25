package com.rudra.financemanager.dto.transaction;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object containing parameters to update an existing transaction.
 * Allows updating the transaction amount, description, and/or category name.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTransactionRequest {

    /**
     * Optional updated transaction amount. Must be positive.
     */
    @Positive(message = "Amount must be a positive value")
    private BigDecimal amount;

    /**
     * Optional updated description/notes for the transaction.
     */
    private String description;

    /**
     * Optional updated category name for the transaction.
     */
    private String category;
}

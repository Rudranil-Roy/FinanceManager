package com.rudra.financemanager.dto.transaction;

import com.rudra.financemanager.entities.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object representing transaction details in API responses.
 * Holds transaction ID, amount, date, category name, description, and type (INCOME or EXPENSE).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

    /**
     * Unique identifier of the transaction.
     */
    private Long id;

    /**
     * The transaction amount.
     */
    private BigDecimal amount;

    /**
     * The date of the transaction.
     */
    private LocalDate date;

    /**
     * The name of the category this transaction belongs to.
     */
    private String category;

    /**
     * The descriptive text/notes of the transaction.
     */
    private String description;

    /**
     * The transaction type (INCOME or EXPENSE).
     */
    private TransactionTypeEnum type;
}

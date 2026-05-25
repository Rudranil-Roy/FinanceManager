package com.rudra.financemanager.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing category details in API responses.
 * Details include identifier, category name, transaction type, and whether it's custom or default.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    /**
     * Unique identifier of the category.
     */
    private Long id;

    /**
     * Name of the category (e.g. "Food", "Salary").
     */
    private String name;

    /**
     * The type of transaction this category supports (INCOME or EXPENSE).
     */
    private TransactionTypeEnum type;

    /**
     * True if the category is custom (user-defined), false if it is a system default.
     */
    @JsonProperty("isCustom")
    private boolean isCustom;
}

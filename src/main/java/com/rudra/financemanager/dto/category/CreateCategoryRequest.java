package com.rudra.financemanager.dto.category;

import com.rudra.financemanager.entities.TransactionTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing parameters to create a new custom category.
 * Holds the category name and its transaction type (INCOME or EXPENSE).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {

    /**
     * The name of the custom category. Must be unique and non-blank.
     */
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    /**
     * The transaction type (INCOME or EXPENSE) associated with the category.
     */
    @NotNull(message = "Category type is required")
    private TransactionTypeEnum type;
}

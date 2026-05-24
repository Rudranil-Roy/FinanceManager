package com.rudra.financemanager.dto.transaction;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTransactionRequest {

    @Positive(message = "Amount must be a positive value")
    private BigDecimal amount;

    private String description;

    private String category;
}

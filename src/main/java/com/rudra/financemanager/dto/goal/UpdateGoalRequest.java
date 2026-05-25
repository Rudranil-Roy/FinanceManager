package com.rudra.financemanager.dto.goal;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object containing parameters to update an existing savings goal.
 * Allows updating target amount and/or target deadline date.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGoalRequest {

    /**
     * Optional updated target monetary amount. Must be positive.
     */
    @Positive(message = "Target amount must be positive")
    private BigDecimal targetAmount;

    /**
     * Optional updated target deadline date. Must be in the future.
     */
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;
}

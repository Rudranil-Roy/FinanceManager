package com.rudra.financemanager.dto.goal;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object containing parameters to create a new savings goal.
 * Sets the target amount, target date, and optional start date.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGoalRequest {

    /**
     * The descriptive name of the savings goal (e.g. "Buy a car").
     */
    @NotBlank(message = "Goal name is required")
    private String goalName;

    /**
     * The target amount to be saved. Must be positive.
     */
    @NotNull(message = "Target amount is required")
    @Positive(message = "Target amount must be positive")
    private BigDecimal targetAmount;

    /**
     * The target deadline date to achieve the goal. Must be in the future.
     */
    @NotNull(message = "Target date is required")
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;

    /**
     * Optional start date for tracking the goal. Defaults to today's date if null.
     */
    private LocalDate startDate;
}

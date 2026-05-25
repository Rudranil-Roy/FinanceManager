package com.rudra.financemanager.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object representing savings goal status in API responses.
 * Includes computed progress details, percentage, and remaining amount towards target.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalResponse {

    /**
     * Unique identifier of the savings goal.
     */
    private Long id;

    /**
     * The descriptive name of the savings goal.
     */
    private String goalName;

    /**
     * The target monetary amount to save.
     */
    private BigDecimal targetAmount;

    /**
     * Target deadline to achieve the savings goal.
     */
    private LocalDate targetDate;

    /**
     * The starting date of the savings goal.
     */
    private LocalDate startDate;

    /**
     * The current progress towards target, computed from net savings since the start date.
     */
    private BigDecimal currentProgress;

    /**
     * Percentage progress achieved (e.g. 75.50 for 75.5%).
     */
    private BigDecimal progressPercentage;

    /**
     * Remaining amount left to reach the target amount.
     */
    private BigDecimal remainingAmount;
}

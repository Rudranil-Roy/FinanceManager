package com.rudra.financemanager.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Data Transfer Object for monthly financial report response.
 * Contains aggregated category-wise income and expenses along with computed net savings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyReportResponse {

    /**
     * The month number of the report (1 to 12).
     */
    private int month;

    /**
     * The year of the report (e.g. 2026).
     */
    private int year;

    /**
     * Map of category names to total income amounts for that category in the month.
     */
    private Map<String, BigDecimal> totalIncome;

    /**
     * Map of category names to total expense amounts for that category in the month.
     */
    private Map<String, BigDecimal> totalExpenses;

    /**
     * Computed net savings for the month (Total Income - Total Expenses).
     */
    private BigDecimal netSavings;
}

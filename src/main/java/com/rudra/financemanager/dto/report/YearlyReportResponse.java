package com.rudra.financemanager.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Data Transfer Object for yearly financial report response.
 * Contains aggregated category-wise income and expenses for the entire year along with net savings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearlyReportResponse {

    /**
     * The year of the report (e.g. 2026).
     */
    private int year;

    /**
     * Map of category names to total income amounts for that category in the year.
     */
    private Map<String, BigDecimal> totalIncome;

    /**
     * Map of category names to total expense amounts for that category in the year.
     */
    private Map<String, BigDecimal> totalExpenses;

    /**
     * Computed net savings for the entire year (Total Yearly Income - Total Yearly Expenses).
     */
    private BigDecimal netSavings;
}

package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.report.MonthlyReportResponse;
import com.rudra.financemanager.dto.report.YearlyReportResponse;

/**
 * Service interface for generating financial reports and summaries.
 */
public interface ReportService {

    /**
     * Generates a detailed monthly report for the current user.
     * Summarizes income/expense totals per category and computes net savings.
     *
     * @param year  The year of the report (e.g. 2026).
     * @param month The month of the report (1 to 12).
     * @return MonthlyReportResponse containing detailed metrics.
     */
    MonthlyReportResponse getMonthlyReport(int year, int month);

    /**
     * Generates a detailed yearly report for the current user.
     * Aggregates category-wise monthly incomes and expenses for the entire year.
     *
     * @param year The year of the report (e.g. 2026).
     * @return YearlyReportResponse containing consolidated metrics.
     */
    YearlyReportResponse getYearlyReport(int year);
}

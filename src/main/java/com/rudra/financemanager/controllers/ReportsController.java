package com.rudra.financemanager.controllers;

import com.rudra.financemanager.dto.report.MonthlyReportResponse;
import com.rudra.financemanager.dto.report.YearlyReportResponse;
import com.rudra.financemanager.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for generating financial reports.
 * Provides endpoints for monthly and yearly financial aggregations.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportService reportService;

    /**
     * Generates a monthly financial report containing income/expense breakdowns
     * by category, total income, total expenses, and net savings for the given month.
     *
     * @param year  The year of the report (e.g., 2026).
     * @param month The month of the report (1 to 12).
     * @return ResponseEntity containing the MonthlyReportResponse.
     */
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(@PathVariable int year,
                                                                  @PathVariable int month) {
        MonthlyReportResponse response = reportService.getMonthlyReport(year, month);
        return ResponseEntity.ok(response);
    }

    /**
     * Generates a yearly financial report containing monthly aggregated incomes,
     * expenses, net savings, total year metrics, and category summaries for the given year.
     *
     * @param year The year of the report (e.g., 2026).
     * @return ResponseEntity containing the YearlyReportResponse.
     */
    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(@PathVariable int year) {
        YearlyReportResponse response = reportService.getYearlyReport(year);
        return ResponseEntity.ok(response);
    }
}

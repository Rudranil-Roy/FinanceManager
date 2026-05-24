package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.report.MonthlyReportResponse;
import com.rudra.financemanager.dto.report.YearlyReportResponse;

public interface ReportService {

    MonthlyReportResponse getMonthlyReport(int year, int month);
    YearlyReportResponse getYearlyReport(int year);
}

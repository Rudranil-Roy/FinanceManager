package com.rudra.financemanager.controllers;

import com.rudra.financemanager.dto.report.MonthlyReportResponse;
import com.rudra.financemanager.dto.report.YearlyReportResponse;
import com.rudra.financemanager.services.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportsController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    void getMonthlyReport_shouldReturn200() throws Exception {
        when(reportService.getMonthlyReport(2024, 1))
                .thenReturn(new MonthlyReportResponse(
                        1,
                        2024,
                        Map.of("Salary", new BigDecimal("3000.00")),
                        Map.of("Food", new BigDecimal("400.00")),
                        new BigDecimal("2600.00")
                ));

        mockMvc.perform(get("/api/reports/monthly/2024/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(1))
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.totalIncome.Salary").value(3000.00));
    }

    @Test
    void getYearlyReport_shouldReturn200() throws Exception {
        when(reportService.getYearlyReport(2024))
                .thenReturn(new YearlyReportResponse(
                        2024,
                        Map.of("Salary", new BigDecimal("36000.00")),
                        Map.of("Rent", new BigDecimal("14400.00")),
                        new BigDecimal("21600.00")
                ));

        mockMvc.perform(get("/api/reports/yearly/2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.totalIncome.Salary").value(36000.00));
    }
}
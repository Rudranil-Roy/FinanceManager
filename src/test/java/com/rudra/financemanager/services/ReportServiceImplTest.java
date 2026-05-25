package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.report.MonthlyReportResponse;
import com.rudra.financemanager.dto.report.YearlyReportResponse;
import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.TransactionEntity;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.BadRequestException;
import com.rudra.financemanager.repositories.TransactionRepository;
import com.rudra.financemanager.security.SessionService;
import com.rudra.financemanager.services.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ReportServiceImplTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private SessionService sessionService;

    private ReportServiceImpl reportService;
    private UserEntity currentUser;

    @BeforeEach
    void setUp() {
        // Clock dependency removed as per the optimized service implementation
        reportService = new ReportServiceImpl(transactionRepository, sessionService);

        currentUser = new UserEntity();
        currentUser.setId(1L);
        currentUser.setUsername("user@example.com");
    }

    @Test
    void getMonthlyReport_shouldAggregateByCategory() {
        CategoryEntity salary = new CategoryEntity();
        salary.setName("Salary");
        salary.setType(TransactionTypeEnum.INCOME);

        CategoryEntity food = new CategoryEntity();
        food.setName("Food");
        food.setType(TransactionTypeEnum.EXPENSE);

        TransactionEntity t1 = new TransactionEntity();
        t1.setDate(LocalDate.of(2024, 1, 10));
        t1.setAmount(new BigDecimal("3000.00"));
        t1.setCategory(salary);

        TransactionEntity t2 = new TransactionEntity();
        t2.setDate(LocalDate.of(2024, 1, 11));
        t2.setAmount(new BigDecimal("400.00"));
        t2.setCategory(food);

        when(sessionService.getCurrentUser()).thenReturn(currentUser);

        when(transactionRepository.findAllWithFilters(
                eq(currentUser),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 1, 31)),
                isNull()
        )).thenReturn(List.of(t1, t2));

        MonthlyReportResponse response = reportService.getMonthlyReport(2024, 1);

        assertEquals(2024, response.getYear());
        assertEquals(1, response.getMonth());
        assertEquals(new BigDecimal("3000.00"), response.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("400.00"), response.getTotalExpenses().get("Food"));
        assertEquals(new BigDecimal("2600.00"), response.getNetSavings());
    }

    @Test
    void getYearlyReport_shouldAggregateAllMatchingTransactions() {
        CategoryEntity salary = new CategoryEntity();
        salary.setName("Salary");
        salary.setType(TransactionTypeEnum.INCOME);

        CategoryEntity rent = new CategoryEntity();
        rent.setName("Rent");
        rent.setType(TransactionTypeEnum.EXPENSE);

        TransactionEntity t1 = new TransactionEntity();
        t1.setDate(LocalDate.of(2024, 2, 1));
        t1.setAmount(new BigDecimal("5000.00"));
        t1.setCategory(salary);

        TransactionEntity t2 = new TransactionEntity();
        t2.setDate(LocalDate.of(2024, 3, 1));
        t2.setAmount(new BigDecimal("1000.00"));
        t2.setCategory(rent);

        when(sessionService.getCurrentUser()).thenReturn(currentUser);

        when(transactionRepository.findAllWithFilters(
                eq(currentUser),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31)),
                isNull()
        )).thenReturn(List.of(t1, t2));

        YearlyReportResponse response = reportService.getYearlyReport(2024);

        assertEquals(2024, response.getYear());
        assertEquals(new BigDecimal("5000.00"), response.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("1000.00"), response.getTotalExpenses().get("Rent"));
        assertEquals(new BigDecimal("4000.00"), response.getNetSavings());
    }

    @Test
    void getMonthlyReport_shouldThrowForInvalidMonth() {
        assertThrows(BadRequestException.class,
                () -> reportService.getMonthlyReport(2024, 13));
    }

    @Test
    void getMonthlyReport_shouldReturnEmptyWhenNoTransactions() {
        when(sessionService.getCurrentUser()).thenReturn(currentUser);

        // Updated mock to reflect the new repository call returning an empty list
        when(transactionRepository.findAllWithFilters(
                eq(currentUser),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 1, 31)),
                isNull()
        )).thenReturn(List.of());

        MonthlyReportResponse response = reportService.getMonthlyReport(2024, 1);

        assertTrue(response.getTotalIncome().isEmpty());
        assertTrue(response.getTotalExpenses().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getNetSavings());
    }
}
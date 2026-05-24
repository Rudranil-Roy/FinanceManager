package com.rudra.financemanager.services.impl;

import com.rudra.financemanager.dto.report.MonthlyReportResponse;
import com.rudra.financemanager.dto.report.YearlyReportResponse;
import com.rudra.financemanager.entities.TransactionEntity;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.BadRequestException;
import com.rudra.financemanager.repositories.TransactionRepository;
import com.rudra.financemanager.security.SessionService;
import com.rudra.financemanager.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final SessionService sessionService;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(int year, int month) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must be between 1 and 12");
        }

        UserEntity currentUser = sessionService.getCurrentUser();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<TransactionEntity> transactions = transactionRepository.findByUserOrderByDateDesc(currentUser).stream()
                .filter(tx -> !tx.getDate().isBefore(start) && !tx.getDate().isAfter(end))
                .toList();

        Map<String, BigDecimal> totalIncome = aggregateByCategory(transactions, TransactionTypeEnum.INCOME);
        Map<String, BigDecimal> totalExpenses = aggregateByCategory(transactions, TransactionTypeEnum.EXPENSE);
        BigDecimal netSavings = calculateNetSavings(transactions);

        return new MonthlyReportResponse(month, year, totalIncome, totalExpenses, netSavings);
    }

    @Override
    @Transactional(readOnly = true)
    public YearlyReportResponse getYearlyReport(int year) {
        UserEntity currentUser = sessionService.getCurrentUser();
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<TransactionEntity> transactions = transactionRepository.findByUserOrderByDateDesc(currentUser).stream()
                .filter(tx -> !tx.getDate().isBefore(start) && !tx.getDate().isAfter(end))
                .toList();

        Map<String, BigDecimal> totalIncome = aggregateByCategory(transactions, TransactionTypeEnum.INCOME);
        Map<String, BigDecimal> totalExpenses = aggregateByCategory(transactions, TransactionTypeEnum.EXPENSE);
        BigDecimal netSavings = calculateNetSavings(transactions);

        return new YearlyReportResponse(year, totalIncome, totalExpenses, netSavings);
    }

    private Map<String, BigDecimal> aggregateByCategory(List<TransactionEntity> transactions, TransactionTypeEnum type) {
        return transactions.stream()
                .filter(tx -> tx.getCategory().getType() == type)
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().getName(),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, TransactionEntity::getAmount, BigDecimal::add)
                ));
    }

    private BigDecimal calculateNetSavings(List<TransactionEntity> transactions) {
        BigDecimal income = transactions.stream()
                .filter(tx -> tx.getCategory().getType() == TransactionTypeEnum.INCOME)
                .map(TransactionEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = transactions.stream()
                .filter(tx -> tx.getCategory().getType() == TransactionTypeEnum.EXPENSE)
                .map(TransactionEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return income.subtract(expenses);
    }
}

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

    @Override
    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(final int year, final int month) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must be between 1 and 12");
        }

        final UserEntity currentUser = sessionService.getCurrentUser();
        final YearMonth yearMonth = YearMonth.of(year, month);
        final LocalDate start = yearMonth.atDay(1);
        final LocalDate end = yearMonth.atEndOfMonth();


        final List<TransactionEntity> transactions = transactionRepository
                .findAllWithFilters(currentUser, start, end, null);

        final Map<String, BigDecimal> totalIncome = aggregateByCategory(transactions, TransactionTypeEnum.INCOME);
        final Map<String, BigDecimal> totalExpenses = aggregateByCategory(transactions, TransactionTypeEnum.EXPENSE);
        final BigDecimal netSavings = calculateNetSavings(transactions);

        return new MonthlyReportResponse(month, year, totalIncome, totalExpenses, netSavings);
    }

    @Override
    @Transactional(readOnly = true)
    public YearlyReportResponse getYearlyReport(final int year) {
        final UserEntity currentUser = sessionService.getCurrentUser();
        final LocalDate start = LocalDate.of(year, 1, 1);
        final LocalDate end = LocalDate.of(year, 12, 31);


        final List<TransactionEntity> transactions = transactionRepository
                .findAllWithFilters(currentUser, start, end, null);

        final Map<String, BigDecimal> totalIncome = aggregateByCategory(transactions, TransactionTypeEnum.INCOME);
        final Map<String, BigDecimal> totalExpenses = aggregateByCategory(transactions, TransactionTypeEnum.EXPENSE);
        final BigDecimal netSavings = calculateNetSavings(transactions);

        return new YearlyReportResponse(year, totalIncome, totalExpenses, netSavings);
    }

    private Map<String, BigDecimal> aggregateByCategory(final List<TransactionEntity> transactions, final TransactionTypeEnum type) {
        return transactions.stream()
                .filter(tx -> tx.getCategory().getType() == type)
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().getName(),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, TransactionEntity::getAmount, BigDecimal::add)
                ));
    }

    private BigDecimal calculateNetSavings(final List<TransactionEntity> transactions) {
        BigDecimal total = BigDecimal.ZERO;

        for (TransactionEntity tx : transactions) {
            if (tx.getCategory().getType() == TransactionTypeEnum.INCOME) {
                total = total.add(tx.getAmount());
            } else {
                total = total.subtract(tx.getAmount());
            }
        }

        return total;
    }
}
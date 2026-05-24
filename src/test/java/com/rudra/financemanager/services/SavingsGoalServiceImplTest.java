package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.goal.CreateGoalRequest;
import com.rudra.financemanager.dto.goal.GoalResponse;
import com.rudra.financemanager.dto.goal.UpdateGoalRequest;
import com.rudra.financemanager.entities.*;
import com.rudra.financemanager.exceptions.ForbiddenException;
import com.rudra.financemanager.repositories.SavingsGoalRepository;
import com.rudra.financemanager.repositories.TransactionRepository;
import com.rudra.financemanager.security.SessionService;
import com.rudra.financemanager.services.impl.SavingsGoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SavingsGoalServiceImplTest {

    @Mock private SavingsGoalRepository savingsGoalRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private SessionService sessionService;

    private Clock fixedClock;

    @InjectMocks private SavingsGoalServiceImpl savingsGoalService;

    private UserEntity currentUser;
    private SavingsGoalEntity goal;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));

        currentUser = new UserEntity();
        currentUser.setId(1L);
        currentUser.setUsername("user@example.com");

        goal = new SavingsGoalEntity();
        goal.setId(1L);
        goal.setGoalName("Emergency Fund");
        goal.setTargetAmount(new BigDecimal("5000.00"));
        goal.setStartDate(LocalDate.of(2025, 1, 1));
        goal.setTargetDate(LocalDate.of(2026, 1, 1));
        goal.setUser(currentUser);
    }

    @Test
    void create_shouldSaveGoal() {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setGoalName("Emergency Fund");
        request.setTargetAmount(new BigDecimal("5000.00"));
        request.setTargetDate(LocalDate.of(2026, 1, 1));
        request.setStartDate(LocalDate.of(2025, 1, 1));

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(savingsGoalRepository.save(any(SavingsGoalEntity.class))).thenAnswer(invocation -> {
            SavingsGoalEntity g = invocation.getArgument(0);
            g.setId(1L);
            return g;
        });

        SavingsGoalServiceImpl service = new SavingsGoalServiceImpl(
                savingsGoalRepository, transactionRepository, sessionService, fixedClock
        );

        GoalResponse response = service.create(request);

        assertEquals(1L, response.getId());
        assertEquals("Emergency Fund", response.getGoalName());
        assertEquals(new BigDecimal("5000.00"), response.getTargetAmount());
    }

    @Test
    void getAll_shouldReturnGoals() {
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(savingsGoalRepository.findByUser(currentUser)).thenReturn(List.of(goal));

        SavingsGoalServiceImpl service = new SavingsGoalServiceImpl(
                savingsGoalRepository, transactionRepository, sessionService, fixedClock
        );

        List<GoalResponse> result = service.getAll();
        assertEquals(1, result.size());
        assertEquals("Emergency Fund", result.get(0).getGoalName());
    }

    @Test
    void getById_shouldThrowWhenGoalBelongsToAnotherUser() {
        UserEntity otherUser = new UserEntity();
        otherUser.setId(2L);

        SavingsGoalEntity otherGoal = new SavingsGoalEntity();
        otherGoal.setId(2L);
        otherGoal.setUser(otherUser);

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(savingsGoalRepository.findById(2L)).thenReturn(java.util.Optional.of(otherGoal));

        SavingsGoalServiceImpl service = new SavingsGoalServiceImpl(
                savingsGoalRepository, transactionRepository, sessionService, fixedClock
        );

        assertThrows(ForbiddenException.class, () -> service.getById(2L));
    }

    @Test
    void update_shouldChangeAmountAndDate() {
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(savingsGoalRepository.findById(1L)).thenReturn(java.util.Optional.of(goal));
        when(savingsGoalRepository.save(any(SavingsGoalEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateGoalRequest request = new UpdateGoalRequest();
        request.setTargetAmount(new BigDecimal("6000.00"));
        request.setTargetDate(LocalDate.of(2026, 2, 1));

        SavingsGoalServiceImpl service = new SavingsGoalServiceImpl(
                savingsGoalRepository, transactionRepository, sessionService, fixedClock
        );

        GoalResponse response = service.update(1L, request);

        assertEquals(new BigDecimal("6000.00"), response.getTargetAmount());
        assertEquals(LocalDate.of(2026, 2, 1), response.getTargetDate());
    }

    @Test
    void delete_shouldDeleteOwnedGoal() {
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(savingsGoalRepository.findById(1L)).thenReturn(java.util.Optional.of(goal));

        SavingsGoalServiceImpl service = new SavingsGoalServiceImpl(
                savingsGoalRepository, transactionRepository, sessionService, fixedClock
        );

        service.delete(1L);
        verify(savingsGoalRepository).delete(goal);
    }

    @Test
    void progressCalculation_shouldConsiderIncomeMinusExpenses() {
        CategoryEntity income = new CategoryEntity();
        income.setType(TransactionTypeEnum.INCOME);
        income.setName("Salary");

        CategoryEntity expense = new CategoryEntity();
        expense.setType(TransactionTypeEnum.EXPENSE);
        expense.setName("Food");

        TransactionEntity t1 = new TransactionEntity();
        t1.setAmount(new BigDecimal("3000.00"));
        t1.setCategory(income);

        TransactionEntity t2 = new TransactionEntity();
        t2.setAmount(new BigDecimal("500.00"));
        t2.setCategory(expense);

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(savingsGoalRepository.findById(1L)).thenReturn(java.util.Optional.of(goal));
        when(transactionRepository.findByUserAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
                eq(currentUser), eq(LocalDate.of(2025, 1, 1)), eq(LocalDate.of(2025, 1, 1))
        )).thenReturn(List.of(t1, t2));

        SavingsGoalServiceImpl service = new SavingsGoalServiceImpl(
                savingsGoalRepository, transactionRepository, sessionService, fixedClock
        );

        GoalResponse response = service.getById(1L);

        assertEquals(new BigDecimal("2500.00"), response.getCurrentProgress());
        assertEquals(new BigDecimal("50.00"), response.getProgressPercentage());
        assertEquals(new BigDecimal("2500.00"), response.getRemainingAmount());
    }
}

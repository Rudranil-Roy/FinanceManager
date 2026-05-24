package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.transaction.CreateTransactionRequest;
import com.rudra.financemanager.dto.transaction.TransactionResponse;
import com.rudra.financemanager.dto.transaction.UpdateTransactionRequest;
import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.TransactionEntity;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.ForbiddenException;
import com.rudra.financemanager.exceptions.ResourceNotFoundException;
import com.rudra.financemanager.repositories.CategoryRepository;
import com.rudra.financemanager.repositories.TransactionRepository;
import com.rudra.financemanager.security.SessionService;
import com.rudra.financemanager.services.impl.TransactionServiceImpl;
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
class TransactionServiceImplTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private SessionService sessionService;

    @InjectMocks private TransactionServiceImpl transactionService;

    private UserEntity currentUser;
    private CategoryEntity salaryCategory;
    private CategoryEntity foodCategory;

    @BeforeEach
    void setUp() {
        currentUser = new UserEntity();
        currentUser.setId(1L);
        currentUser.setUsername("user@example.com");

        salaryCategory = new CategoryEntity();
        salaryCategory.setId(10L);
        salaryCategory.setName("Salary");
        salaryCategory.setType(TransactionTypeEnum.INCOME);
        salaryCategory.setCustom(false);
        salaryCategory.setUser(null);

        foodCategory = new CategoryEntity();
        foodCategory.setId(11L);
        foodCategory.setName("Food");
        foodCategory.setType(TransactionTypeEnum.EXPENSE);
        foodCategory.setCustom(false);
        foodCategory.setUser(null);
    }

    @Test
    void create_shouldSaveTransaction() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(new BigDecimal("50000.00"));
        request.setDate(LocalDate.of(2024, 1, 15));
        request.setCategory("Salary");
        request.setDescription("January Salary");

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findByUserIsNull()).thenReturn(List.of(salaryCategory, foodCategory));
        when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(invocation -> {
            TransactionEntity t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TransactionResponse response = transactionService.create(request);

        assertEquals(1L, response.getId());
        assertEquals("Salary", response.getCategory());
        assertEquals(TransactionTypeEnum.INCOME, response.getType());
        assertEquals(new BigDecimal("50000.00"), response.getAmount());
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void getAll_shouldReturnFilteredTransactions() {
        TransactionEntity tx1 = new TransactionEntity();
        tx1.setId(1L);
        tx1.setAmount(new BigDecimal("50000.00"));
        tx1.setDate(LocalDate.of(2024, 1, 15));
        tx1.setDescription("January Salary");
        tx1.setUser(currentUser);
        tx1.setCategory(salaryCategory);

        TransactionEntity tx2 = new TransactionEntity();
        tx2.setId(2L);
        tx2.setAmount(new BigDecimal("300.00"));
        tx2.setDate(LocalDate.of(2024, 1, 20));
        tx2.setDescription("Food");
        tx2.setUser(currentUser);
        tx2.setCategory(foodCategory);

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findById(10L)).thenReturn(java.util.Optional.of(salaryCategory));
        when(transactionRepository.findByUserOrderByDateDesc(currentUser)).thenReturn(List.of(tx1, tx2));

        var result = transactionService.getAll(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                10L
        );

        assertEquals(1, result.size());
        assertEquals("Salary", result.get(0).getCategory());
    }

    @Test
    void update_shouldChangeAmountAndDescription() {
        TransactionEntity existing = new TransactionEntity();
        existing.setId(1L);
        existing.setUser(currentUser);
        existing.setAmount(new BigDecimal("1000.00"));
        existing.setDate(LocalDate.of(2024, 1, 15));
        existing.setDescription("Old");
        existing.setCategory(salaryCategory);

        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setAmount(new BigDecimal("2000.00"));
        request.setDescription("New");

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.update(1L, request);

        assertEquals(new BigDecimal("2000.00"), response.getAmount());
        assertEquals("New", response.getDescription());
    }

    @Test
    void update_shouldThrowWhenTransactionBelongsToAnotherUser() {
        UserEntity otherUser = new UserEntity();
        otherUser.setId(2L);

        TransactionEntity existing = new TransactionEntity();
        existing.setId(1L);
        existing.setUser(otherUser);
        existing.setAmount(new BigDecimal("1000.00"));
        existing.setDate(LocalDate.of(2024, 1, 15));
        existing.setCategory(salaryCategory);

        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setAmount(new BigDecimal("2000.00"));

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));

        assertThrows(ForbiddenException.class, () -> transactionService.update(1L, request));
    }

    @Test
    void delete_shouldThrowWhenNotOwned() {
        UserEntity otherUser = new UserEntity();
        otherUser.setId(2L);

        TransactionEntity existing = new TransactionEntity();
        existing.setId(1L);
        existing.setUser(otherUser);

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));

        assertThrows(ForbiddenException.class, () -> transactionService.delete(1L));
    }

    @Test
    void delete_shouldThrowWhenMissing() {
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(transactionRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.delete(1L));
    }
}
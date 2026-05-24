package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.category.CategoryResponse;
import com.rudra.financemanager.dto.category.CreateCategoryRequest;
import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.BadRequestException;
import com.rudra.financemanager.exceptions.ConflictException;
import com.rudra.financemanager.repositories.CategoryRepository;
import com.rudra.financemanager.repositories.TransactionRepository;
import com.rudra.financemanager.security.SessionService;
import com.rudra.financemanager.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private SessionService sessionService;

    @InjectMocks private CategoryServiceImpl categoryService;

    private UserEntity currentUser;
    private CategoryEntity salaryCategory;
    private CategoryEntity customCategory;

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

        customCategory = new CategoryEntity();
        customCategory.setId(11L);
        customCategory.setName("SideIncome");
        customCategory.setType(TransactionTypeEnum.INCOME);
        customCategory.setCustom(true);
        customCategory.setUser(currentUser);
    }

    @Test
    void getAll_shouldReturnDefaultAndUserCategories() {
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findByUserIsNull()).thenReturn(List.of(salaryCategory));
        when(categoryRepository.findByUser(currentUser)).thenReturn(List.of(customCategory));

        List<CategoryResponse> result = categoryService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void create_shouldSaveCustomCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Travel");
        request.setType(TransactionTypeEnum.EXPENSE);

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findByUserIsNull()).thenReturn(List.of(salaryCategory));
        when(categoryRepository.findByUser(currentUser)).thenReturn(List.of(customCategory));
        when(categoryRepository.save(any(CategoryEntity.class))).thenAnswer(invocation -> {
            CategoryEntity c = invocation.getArgument(0);
            c.setId(99L);
            return c;
        });

        CategoryResponse response = categoryService.create(request);

        assertEquals(99L, response.getId());
        assertEquals("Travel", response.getName());
        assertTrue(response.isCustom());
    }

    @Test
    void create_shouldThrowConflictOnDuplicateName() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Salary");
        request.setType(TransactionTypeEnum.INCOME);

        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findByUserIsNull()).thenReturn(List.of(salaryCategory));

        assertThrows(ConflictException.class, () -> categoryService.create(request));
    }

    @Test
    void deleteByName_shouldRejectDefaultCategory() {
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findByUserIsNull()).thenReturn(List.of(salaryCategory));

        assertThrows(BadRequestException.class, () -> categoryService.deleteByName("Salary"));
    }

    @Test
    void deleteByName_shouldDeleteCustomCategoryWhenUnused() {
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findByUserIsNull()).thenReturn(List.of(salaryCategory));
        when(categoryRepository.findByUser(currentUser)).thenReturn(List.of(customCategory));
        when(transactionRepository.existsByCategory(customCategory)).thenReturn(false);

        categoryService.deleteByName("SideIncome");

        verify(categoryRepository).delete(customCategory);
    }

    @Test
    void deleteByName_shouldThrowConflictWhenCategoryUsedByTransactions() {
        when(sessionService.getCurrentUser()).thenReturn(currentUser);
        when(categoryRepository.findByUserIsNull()).thenReturn(List.of(salaryCategory));
        when(categoryRepository.findByUser(currentUser)).thenReturn(List.of(customCategory));
        when(transactionRepository.existsByCategory(customCategory)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.deleteByName("SideIncome"));
    }
}

package com.rudra.financemanager.services.impl;

import com.rudra.financemanager.dto.transaction.CreateTransactionRequest;
import com.rudra.financemanager.dto.transaction.TransactionResponse;
import com.rudra.financemanager.dto.transaction.UpdateTransactionRequest;
import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.TransactionEntity;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.BadRequestException;
import com.rudra.financemanager.exceptions.ForbiddenException;
import com.rudra.financemanager.exceptions.ResourceNotFoundException;
import com.rudra.financemanager.repositories.CategoryRepository;
import com.rudra.financemanager.repositories.TransactionRepository;
import com.rudra.financemanager.security.SessionService;
import com.rudra.financemanager.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final SessionService sessionService;

    @Override
    @Transactional
    public TransactionResponse create(final CreateTransactionRequest request) {
        final UserEntity currentUser = sessionService.getCurrentUser();

        if (request.getDate() == null) {
            throw new BadRequestException("Date is required");
        }
        if (request.getDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Date cannot be in the future");
        }

        final CategoryEntity category = findAccessibleCategoryByName(request.getCategory(), currentUser);

        final TransactionEntity transactionEntity = TransactionEntity.builder()
                .amount(request.getAmount())
                .date(request.getDate())
                .description(request.getDescription())
                .category(category)
                .user(currentUser)
                .build();

        return toResponse(transactionRepository.save(transactionEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAll(final LocalDate startDate, final LocalDate endDate, final Long categoryId) {
        final UserEntity currentUser = sessionService.getCurrentUser();

        CategoryEntity filterCategory = null;
        if (categoryId != null) {
            filterCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            if (!isAccessibleCategory(filterCategory, currentUser)) {
                throw new ResourceNotFoundException("Category not found");
            }
        }

        final Long targetCategoryId = filterCategory != null ? filterCategory.getId() : null;

        return transactionRepository.findAllWithFilters(currentUser, startDate, endDate, targetCategoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponse update(final Long id, final UpdateTransactionRequest request) {
        final UserEntity currentUser = sessionService.getCurrentUser();
        final TransactionEntity transaction = getOwnedTransaction(id, currentUser);

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            final CategoryEntity newCategory = findAccessibleCategoryByName(request.getCategory(), currentUser);
            transaction.setCategory(newCategory);
        }

        return toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public void delete(final Long id) {
        final UserEntity currentUser = sessionService.getCurrentUser();
        final TransactionEntity transaction = getOwnedTransaction(id, currentUser);

        transactionRepository.delete(transaction);
    }

    private TransactionEntity getOwnedTransaction(final Long id, final UserEntity currentUser) {
        final TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access denied to this transaction");
        }

        return transaction;
    }

    private CategoryEntity findAccessibleCategoryByName(final String categoryName, final UserEntity currentUser) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new BadRequestException("Category name is required");
        }

        return categoryRepository.findAccessibleCategoryByName(categoryName.trim(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryName));
    }

    private boolean isAccessibleCategory(final CategoryEntity category, final UserEntity currentUser) {
        return category.getUser() == null || category.getUser().getId().equals(currentUser.getId());
    }

    private TransactionResponse toResponse(final TransactionEntity transaction) {
        final TransactionTypeEnum type = transaction.getCategory().getType() == TransactionTypeEnum.INCOME
                ? TransactionTypeEnum.INCOME
                : TransactionTypeEnum.EXPENSE;

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getCategory().getName(),
                transaction.getDescription(),
                type
        );
    }
}
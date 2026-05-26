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

/**
 * Implementation of {@link TransactionService}.
 * Contains business operations for managing transactions, filtering by category/date,
 * and performing ownership validation.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final SessionService sessionService;

    /**
     * Creates a new transaction (INCOME or EXPENSE) for the currently authenticated user.
     * Dates cannot be in the future, and the referenced category name must be accessible.
     *
     * @param request DTO containing values to create the transaction.
     * @return TransactionResponse containing detailed created transaction details.
     * @throws BadRequestException       if the date is null or in the future.
     * @throws ResourceNotFoundException if the specified category name is not found.
     */
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

    /**
     * Retrieves all transactions belonging to the current user, optionally filtered by category and dates.
     *
     * @param startDate  Optional filter for transaction date (on or after).
     * @param endDate    Optional filter for transaction date (on or before).
     * @param categoryId Optional filter for a specific category ID.
     * @param category   Optional filter for a specific category name.
     * @return List of TransactionResponse DTOs.
     * @throws ResourceNotFoundException if a specified category ID or name is not found or is inaccessible.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAll(final LocalDate startDate, final LocalDate endDate, final Long categoryId, final String category) {
        final UserEntity currentUser = sessionService.getCurrentUser();

        CategoryEntity filterCategory = null;
        if (categoryId != null) {
            filterCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            if (!isAccessibleCategory(filterCategory, currentUser)) {
                throw new ResourceNotFoundException("Category not found");
            }
        } else if (category != null && !category.isBlank()) {
            filterCategory = categoryRepository.findAccessibleCategoryByName(category.trim(), currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        final Long targetCategoryId = filterCategory != null ? filterCategory.getId() : null;

        return transactionRepository.findAllWithFilters(currentUser, startDate, endDate, targetCategoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Updates fields of an existing transaction owned by the authenticated user.
     * The transaction date and transaction type remain immutable.
     *
     * @param id      The transaction ID to update.
     * @param request DTO containing potential updates.
     * @return TransactionResponse containing detailed updated transaction details.
     * @throws ResourceNotFoundException if the transaction or referenced category is not found.
     * @throws ForbiddenException        if the current user does not own the transaction.
     */
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

    /**
     * Deletes a transaction owned by the current user.
     *
     * @param id The ID of the transaction to delete.
     * @throws ResourceNotFoundException if the transaction does not exist.
     * @throws ForbiddenException        if the user does not own the transaction.
     */
    @Override
    @Transactional
    public void delete(final Long id) {
        final UserEntity currentUser = sessionService.getCurrentUser();
        final TransactionEntity transaction = getOwnedTransaction(id, currentUser);

        transactionRepository.delete(transaction);
    }

    /**
     * Helper to retrieve a transaction by ID and assert that the current user owns it.
     *
     * @param id          The transaction ID.
     * @param currentUser The authenticated user.
     * @return The transaction entity.
     * @throws ResourceNotFoundException if not found.
     * @throws ForbiddenException        if not owned by the user.
     */
    private TransactionEntity getOwnedTransaction(final Long id, final UserEntity currentUser) {
        final TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access denied to this transaction");
        }

        return transaction;
    }

    /**
     * Helper to retrieve a category by name, asserting that it is accessible to the user.
     *
     * @param categoryName The category name.
     * @param currentUser  The authenticated user.
     * @return The CategoryEntity if accessible.
     * @throws BadRequestException       if categoryName is null or blank.
     * @throws ResourceNotFoundException if the category is not found.
     */
    private CategoryEntity findAccessibleCategoryByName(final String categoryName, final UserEntity currentUser) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new BadRequestException("Category name is required");
        }

        return categoryRepository.findAccessibleCategoryByName(categoryName.trim(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryName));
    }

    /**
     * Helper to check if a category is system default or belongs to the specified user.
     *
     * @param category    The category to check.
     * @param currentUser The user to compare against.
     * @return true if accessible, false otherwise.
     */
    private boolean isAccessibleCategory(final CategoryEntity category, final UserEntity currentUser) {
        return category.getUser() == null || category.getUser().getId().equals(currentUser.getId());
    }

    /**
     * Maps a {@link TransactionEntity} to {@link TransactionResponse}.
     *
     * @param transaction The TransactionEntity to map.
     * @return Constructed TransactionResponse.
     */
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
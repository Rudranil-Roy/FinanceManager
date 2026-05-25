package com.rudra.financemanager.services.impl;

import com.rudra.financemanager.dto.category.CategoryResponse;
import com.rudra.financemanager.dto.category.CreateCategoryRequest;
import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.BadRequestException;
import com.rudra.financemanager.exceptions.ConflictException;
import com.rudra.financemanager.exceptions.ForbiddenException;
import com.rudra.financemanager.exceptions.ResourceNotFoundException;
import com.rudra.financemanager.repositories.CategoryRepository;
import com.rudra.financemanager.repositories.TransactionRepository;
import com.rudra.financemanager.security.SessionService;
import com.rudra.financemanager.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link CategoryService}.
 * Executes business logic for managing, validating, creating, and deleting financial categories.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final SessionService sessionService;

    /**
     * Retrieves all categories accessible to the current user.
     * Consolidates system default categories and the user's personal custom categories.
     *
     * @return List of accessible CategoryResponse DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        final UserEntity currentUser = sessionService.getCurrentUser();

        return categoryRepository.findAllAccessibleCategories(currentUser)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Creates a new custom category for the current user.
     * Ensures the category name is unique within the user's accessible namespace.
     *
     * @param request DTO containing parameters for the new category.
     * @return CategoryResponse detailed DTO.
     * @throws BadRequestException if the category name is null or blank.
     * @throws ConflictException   if the category name already exists.
     */
    @Override
    @Transactional
    public CategoryResponse create(final CreateCategoryRequest request) {
        final UserEntity currentUser = sessionService.getCurrentUser();

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Category name is required");
        }

        final String trimmedName = request.getName().trim();

        if (categoryRepository.existsByAccessibleName(trimmedName, currentUser)) {
            throw new ConflictException("Category name already exists");
        }

        final CategoryEntity category = CategoryEntity.builder()
                .name(trimmedName)
                .type(request.getType())
                .isCustom(true)
                .user(currentUser)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    /**
     * Deletes a custom category by its name.
     * Validates that the category belongs to the current user, is custom, and is not
     * currently referenced by any existing transactions.
     *
     * @param name Name of the category to be deleted.
     * @throws BadRequestException       if the category name is null, blank, or is a default category.
     * @throws ResourceNotFoundException if the category does not exist.
     * @throws ForbiddenException        if the category belongs to another user.
     * @throws ConflictException         if the category is in use by transactions.
     */
    @Override
    @Transactional
    public void deleteByName(final String name) {
        final UserEntity currentUser = sessionService.getCurrentUser();

        if (name == null || name.isBlank()) {
            throw new BadRequestException("Category name is required");
        }

        final CategoryEntity category = categoryRepository.findAccessibleCategoryByName(name.trim(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (category.getUser() == null) {
            throw new BadRequestException("Default categories cannot be deleted");
        }

        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access denied to this category");
        }

        if (transactionRepository.existsByCategory(category)) {
            throw new ConflictException("Category is currently referenced by transactions");
        }

        categoryRepository.delete(category);
    }

    /**
     * Maps a {@link CategoryEntity} to a {@link CategoryResponse}.
     *
     * @param category The CategoryEntity.
     * @return The CategoryResponse.
     */
    private CategoryResponse toResponse(final CategoryEntity category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.isCustom()
        );
    }
}
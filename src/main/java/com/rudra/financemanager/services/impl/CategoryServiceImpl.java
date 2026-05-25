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

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final SessionService sessionService;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        final UserEntity currentUser = sessionService.getCurrentUser();

        return categoryRepository.findAllAccessibleCategories(currentUser)
                .stream()
                .map(this::toResponse)
                .toList();
    }

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

    private CategoryResponse toResponse(final CategoryEntity category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.isCustom()
        );
    }
}
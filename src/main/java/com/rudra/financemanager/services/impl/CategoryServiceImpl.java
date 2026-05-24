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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final SessionService sessionService;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        UserEntity currentUser = sessionService.getCurrentUser();

        List<CategoryEntity> categories = new ArrayList<>(categoryRepository.findByUserIsNull());
        categories.addAll(categoryRepository.findByUser(currentUser));

        return categories.stream()
                .sorted(Comparator.comparing(CategoryEntity::getType).thenComparing(CategoryEntity::getName))
                .map(this::toResponse)
                .toList();
    }


    @Override
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        UserEntity currentUser = sessionService.getCurrentUser();

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Category name is required");
        }

        boolean nameExists = categoryRepository.findByUserIsNull().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(request.getName()))
                || categoryRepository.findByUser(currentUser).stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(request.getName()));

        if (nameExists) {
            throw new ConflictException("Category name already exists");
        }

        CategoryEntity category = CategoryEntity.builder()
                .name(request.getName())
                .type(request.getType())
                .isCustom(true)
                .user(currentUser)
                .build();

        CategoryEntity saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteByName(String name) {
        UserEntity currentUser = sessionService.getCurrentUser();

        CategoryEntity category = findAccessibleCategoryByName(name, currentUser)
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

    private Optional<CategoryEntity> findAccessibleCategoryByName(String name, UserEntity currentUser) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        Optional<CategoryEntity> defaultCategory = categoryRepository.findByUserIsNull().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();

        if (defaultCategory.isPresent()) {
            return defaultCategory;
        }

        return categoryRepository.findByUser(currentUser).stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    private CategoryResponse toResponse(CategoryEntity category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.isCustom()
        );
    }
}

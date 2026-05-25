package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.category.CategoryResponse;
import com.rudra.financemanager.dto.category.CreateCategoryRequest;

import java.util.List;

/**
 * Service interface for managing category operations.
 * Allows retrieving, creating, and deleting categories for the currently authenticated user.
 */
public interface CategoryService {

    /**
     * Retrieves all categories accessible to the current user.
     * Includes default system categories and custom user categories.
     *
     * @return List of CategoryResponse DTOs.
     */
    List<CategoryResponse> getAll();

    /**
     * Creates a new custom category for the current user.
     *
     * @param createCategoryRequest DTO containing category specifications.
     * @return CategoryResponse containing details of the created category.
     */
    CategoryResponse create(CreateCategoryRequest createCategoryRequest);

    /**
     * Deletes a user's custom category by its name.
     * Fails if the category is system default or referenced by any transactions.
     *
     * @param name Name of the category to be deleted.
     */
    void deleteByName(String name);
}

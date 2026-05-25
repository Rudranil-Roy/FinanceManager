package com.rudra.financemanager.controllers;

import com.rudra.financemanager.dto.category.CategoryResponse;
import com.rudra.financemanager.dto.category.CreateCategoryRequest;
import com.rudra.financemanager.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing financial categories.
 * Provides endpoints to list, create, and delete categories.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Retrieves all categories accessible to the current user.
     * This includes all system default categories and the user's custom categories.
     *
     * @return ResponseEntity containing a list of CategoryResponse DTOs.
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        List<CategoryResponse> response = categoryService.getAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new custom category for the authenticated user.
     *
     * @param request DTO containing category details (name, isDefault).
     * @return ResponseEntity with the created category's details and HTTP 201 status.
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Deletes a user's custom category by its name.
     * Throws an exception if the category is system default or currently in use by transactions.
     *
     * @param name Name of the category to be deleted.
     * @return ResponseEntity with a 200 OK status on success.
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        categoryService.deleteByName(name);
        return ResponseEntity.ok().build();
    }
}

package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.category.CategoryResponse;
import com.rudra.financemanager.dto.category.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAll();
    CategoryResponse create(CreateCategoryRequest createCategoryRequest);
    void deleteByName(String name);
}

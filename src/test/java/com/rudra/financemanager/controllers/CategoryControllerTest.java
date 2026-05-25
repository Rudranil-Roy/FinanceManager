package com.rudra.financemanager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rudra.financemanager.dto.category.CategoryResponse;
import com.rudra.financemanager.dto.category.CreateCategoryRequest;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getAll_shouldReturn200() throws Exception {
        when(categoryService.getAll()).thenReturn(List.of(
                new CategoryResponse(1L, "Salary", TransactionTypeEnum.INCOME, false)
        ));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Salary"));
    }

    @Test
    void create_shouldReturn201() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Travel");
        request.setType(TransactionTypeEnum.EXPENSE);

        when(categoryService.create(any(CreateCategoryRequest.class)))
                .thenReturn(new CategoryResponse(2L, "Travel", TransactionTypeEnum.EXPENSE, true));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Travel"))
                .andExpect(jsonPath("$.isCustom").value(true));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/categories/Travel"))
                .andExpect(status().isOk());
    }
}
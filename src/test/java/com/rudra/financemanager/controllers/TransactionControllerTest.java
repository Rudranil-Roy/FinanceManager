package com.rudra.financemanager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rudra.financemanager.dto.transaction.CreateTransactionRequest;
import com.rudra.financemanager.dto.transaction.TransactionResponse;
import com.rudra.financemanager.dto.transaction.UpdateTransactionRequest;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.services.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void create_shouldReturn201() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(new BigDecimal("50000.00"));
        request.setDate(LocalDate.of(2024, 1, 15));
        request.setCategory("Salary");
        request.setDescription("January Salary");

        when(transactionService.create(any(CreateTransactionRequest.class)))
                .thenReturn(new TransactionResponse(
                        1L,
                        new BigDecimal("50000.00"),
                        LocalDate.of(2024, 1, 15),
                        "Salary",
                        "January Salary",
                        TransactionTypeEnum.INCOME
                ));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("Salary"));
    }

    @Test
    void getAll_shouldReturn200() throws Exception {
        when(transactionService.getAll(null, null, null)).thenReturn(List.of(
                new TransactionResponse(
                        1L,
                        new BigDecimal("50000.00"),
                        LocalDate.of(2024, 1, 15),
                        "Salary",
                        "January Salary",
                        TransactionTypeEnum.INCOME
                )
        ));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setAmount(new BigDecimal("60000.00"));
        request.setDescription("Updated January Salary");

        when(transactionService.update(anyLong(), any(UpdateTransactionRequest.class)))
                .thenReturn(new TransactionResponse(
                        1L,
                        new BigDecimal("60000.00"),
                        LocalDate.of(2024, 1, 15),
                        "Salary",
                        "Updated January Salary",
                        TransactionTypeEnum.INCOME
                ));

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(60000.00));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk());
    }
}
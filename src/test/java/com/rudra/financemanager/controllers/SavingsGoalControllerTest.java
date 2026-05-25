package com.rudra.financemanager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rudra.financemanager.dto.goal.CreateGoalRequest;
import com.rudra.financemanager.dto.goal.GoalResponse;
import com.rudra.financemanager.dto.goal.UpdateGoalRequest;
import com.rudra.financemanager.services.SavingsGoalService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SavingsGoalController.class)
@AutoConfigureMockMvc(addFilters = false)
class SavingsGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SavingsGoalService savingsGoalService;

    @Test
    void create_shouldReturn201() throws Exception {

        CreateGoalRequest request = new CreateGoalRequest();
        request.setGoalName("Emergency Fund");
        request.setTargetAmount(new BigDecimal("5000.00"));
        request.setTargetDate(LocalDate.now().plusMonths(6));
        request.setStartDate(LocalDate.of(2025, 1, 1));

        when(savingsGoalService.create(any(CreateGoalRequest.class)))
                .thenReturn(new GoalResponse(
                        1L,
                        "Emergency Fund",
                        new BigDecimal("5000.00"),
                        LocalDate.now().plusMonths(6),
                        LocalDate.of(2025, 1, 1),
                        new BigDecimal("1000.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("4000.00")
                ));

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"));
    }

    @Test
    void getAll_shouldReturn200() throws Exception {

        when(savingsGoalService.getAll()).thenReturn(List.of(
                new GoalResponse(
                        1L,
                        "Emergency Fund",
                        new BigDecimal("5000.00"),
                        LocalDate.now().plusMonths(6),
                        LocalDate.of(2025, 1, 1),
                        new BigDecimal("1000.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("4000.00")
                )
        ));

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].goalName").value("Emergency Fund"));
    }

    @Test
    void getById_shouldReturn200() throws Exception {

        when(savingsGoalService.getById(anyLong()))
                .thenReturn(new GoalResponse(
                        1L,
                        "Emergency Fund",
                        new BigDecimal("5000.00"),
                        LocalDate.now().plusMonths(6),
                        LocalDate.of(2025, 1, 1),
                        new BigDecimal("1000.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("4000.00")
                ));

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_shouldReturn200() throws Exception {

        UpdateGoalRequest request = new UpdateGoalRequest();
        request.setTargetAmount(new BigDecimal("6000.00"));
        request.setTargetDate(LocalDate.now().plusMonths(6));

        when(savingsGoalService.update(anyLong(), any(UpdateGoalRequest.class)))
                .thenReturn(new GoalResponse(
                        1L,
                        "Emergency Fund",
                        new BigDecimal("6000.00"),
                        LocalDate.now().plusMonths(6),
                        LocalDate.of(2025, 1, 1),
                        new BigDecimal("1000.00"),
                        new BigDecimal("16.67"),
                        new BigDecimal("5000.00")
                ));

        mockMvc.perform(put("/api/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetAmount").value(6000.00));
    }

    @Test
    void delete_shouldReturn200() throws Exception {

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isOk());
    }
}
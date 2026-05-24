package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.goal.CreateGoalRequest;
import com.rudra.financemanager.dto.goal.GoalResponse;
import com.rudra.financemanager.dto.goal.UpdateGoalRequest;

import java.util.List;

public interface SavingsGoalService {

    GoalResponse create(CreateGoalRequest createGoalRequest);
    List<GoalResponse> getAll();
    GoalResponse getById(Long id);
    GoalResponse update(Long id, UpdateGoalRequest updateGoalRequest);
    void delete(Long id);
}

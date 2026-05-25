package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.goal.CreateGoalRequest;
import com.rudra.financemanager.dto.goal.GoalResponse;
import com.rudra.financemanager.dto.goal.UpdateGoalRequest;

import java.util.List;

/**
 * Service interface for managing savings goals operations.
 */
public interface SavingsGoalService {

    /**
     * Creates a new savings goal for the current authenticated user.
     *
     * @param createGoalRequest DTO containing parameters for the new savings goal.
     * @return GoalResponse detailed DTO.
     */
    GoalResponse create(CreateGoalRequest createGoalRequest);

    /**
     * Retrieves all savings goals belonging to the current user, along with computed progress stats.
     *
     * @return List of GoalResponse DTOs.
     */
    List<GoalResponse> getAll();

    /**
     * Retrieves details of a specific savings goal by its unique ID.
     *
     * @param id The identifier of the savings goal.
     * @return GoalResponse detailed DTO.
     */
    GoalResponse getById(Long id);

    /**
     * Updates an existing savings goal's target amount and/or target deadline date.
     *
     * @param id                The identifier of the savings goal to update.
     * @param updateGoalRequest DTO containing updated goal parameters.
     * @return GoalResponse detailed DTO.
     */
    GoalResponse update(Long id, UpdateGoalRequest updateGoalRequest);

    /**
     * Deletes a savings goal by its unique ID.
     *
     * @param id The identifier of the savings goal to delete.
     */
    void delete(Long id);
}

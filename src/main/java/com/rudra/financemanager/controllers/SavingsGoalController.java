package com.rudra.financemanager.controllers;

import com.rudra.financemanager.dto.goal.CreateGoalRequest;
import com.rudra.financemanager.dto.goal.GoalResponse;
import com.rudra.financemanager.dto.goal.UpdateGoalRequest;
import com.rudra.financemanager.services.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing a user's savings goals.
 * Provides endpoints for creating, retrieving, updating, and deleting goals.
 */
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    /**
     * Creates a new savings goal for the current authenticated user.
     *
     * @param request DTO containing the savings goal specifications (name, targetAmount, targetDate, etc.).
     * @return ResponseEntity with the created savings goal's details and HTTP 201 status.
     */
    @PostMapping
    public ResponseEntity<GoalResponse> create(@Valid @RequestBody CreateGoalRequest request) {
        GoalResponse response = savingsGoalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all savings goals belonging to the authenticated user.
     * Includes computed current savings and progress percentages.
     *
     * @return ResponseEntity with a list of all savings goals.
     */
    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAll() {
        List<GoalResponse> response = savingsGoalService.getAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves details of a specific savings goal by its ID.
     * Ensures strict user isolation/ownership.
     *
     * @param id The ID of the savings goal to retrieve.
     * @return ResponseEntity with the requested savings goal details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getById(@PathVariable Long id) {
        GoalResponse response = savingsGoalService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing savings goal's target amount or target date.
     *
     * @param id      The ID of the savings goal to update.
     * @param request DTO containing updated savings goal parameters.
     * @return ResponseEntity containing the updated savings goal details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateGoalRequest request) {
        GoalResponse response = savingsGoalService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a savings goal by its ID.
     *
     * @param id The ID of the savings goal to delete.
     * @return ResponseEntity with a 200 OK status on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        savingsGoalService.delete(id);
        return ResponseEntity.ok().build();
    }
}

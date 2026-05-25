package com.rudra.financemanager.repositories;

import com.rudra.financemanager.entities.SavingsGoalEntity;
import com.rudra.financemanager.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link SavingsGoalEntity}.
 * Manages standard persistence actions and custom queries for user savings goals.
 */
public interface SavingsGoalRepository extends JpaRepository<SavingsGoalEntity, Long> {

    /**
     * Retrieves all savings goals belonging to a specific user, ordered by ID descending.
     *
     * @param user The owner of the savings goals.
     * @return List of savings goal entities.
     */
    List<SavingsGoalEntity> findByUserOrderByIdDesc(UserEntity user);
}

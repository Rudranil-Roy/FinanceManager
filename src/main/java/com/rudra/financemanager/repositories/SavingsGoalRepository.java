package com.rudra.financemanager.repositories;

import com.rudra.financemanager.entities.SavingsGoalEntity;
import com.rudra.financemanager.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoalEntity, Long> {



    List<SavingsGoalEntity> findByUserOrderByIdDesc(UserEntity user);
}

package com.rudra.financemanager.repositories;

import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.TransactionEntity;
import com.rudra.financemanager.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByUserOrderByDateDesc(UserEntity user);

    List<TransactionEntity> findByUserAndDateBetweenOrderByDateDesc(UserEntity user, LocalDate startDate, LocalDate endDate);

    List<TransactionEntity> findByUserAndCategoryOrderByDateDesc(UserEntity user, CategoryEntity category);

    List<TransactionEntity> findByUserAndDateBetweenAndCategoryOrderByDateDesc(
            UserEntity user,
            LocalDate startDate,
            LocalDate endDate,
            CategoryEntity category
    );

    List<TransactionEntity> findByUserAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
            UserEntity user,
            LocalDate startDate,
            LocalDate endDate
    );

    boolean existsByCategory(CategoryEntity category);
}

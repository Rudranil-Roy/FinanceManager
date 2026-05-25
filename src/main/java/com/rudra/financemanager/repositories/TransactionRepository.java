package com.rudra.financemanager.repositories;

import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.TransactionEntity;
import com.rudra.financemanager.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {


    @Query("SELECT t FROM TransactionEntity t WHERE t.user = :user " +
            "AND (CAST(:startDate AS date) IS NULL OR t.date >= :startDate) " +
            "AND (CAST(:endDate AS date) IS NULL OR t.date <= :endDate) " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
            "ORDER BY t.date DESC, t.id DESC")
    List<TransactionEntity> findAllWithFilters(
            @Param("user") UserEntity user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId
    );

    @Query("SELECT COALESCE(SUM(CASE WHEN t.category.type = 'INCOME' THEN t.amount ELSE -t.amount END), 0) " +
            "FROM TransactionEntity t WHERE t.user = :user AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal calculateNetSavingsForPeriod(
            @Param("user") UserEntity user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByCategory(CategoryEntity category);
}

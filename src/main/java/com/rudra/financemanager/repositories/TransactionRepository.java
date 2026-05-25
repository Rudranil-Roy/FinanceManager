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

/**
 * Spring Data JPA repository for {@link TransactionEntity}.
 * Optimized for database-level aggregations and flexible transactions querying with filters.
 */
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    /**
     * Retrieves transactions belonging to a specific user, with optional filters for start date,
     * end date, and category ID. Results are ordered by date and ID descending.
     *
     * @param user       The owner of the transactions.
     * @param startDate  Optional filter to show transactions on or after this date.
     * @param endDate    Optional filter to show transactions on or before this date.
     * @param categoryId Optional filter to show transactions matching this category ID.
     * @return List of filtered transaction entities.
     */
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

    /**
     * Calculates the net savings (Total Income - Total Expenses) for a user over a specific date range.
     * Pushes down the aggregation calculations entirely to the database layer.
     *
     * @param user      The target user.
     * @param startDate The start date of the period.
     * @param endDate   The end date of the period.
     * @return Net savings BigDecimal for the specified period.
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN t.category.type = 'INCOME' THEN t.amount ELSE -t.amount END), 0) " +
            "FROM TransactionEntity t WHERE t.user = :user AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal calculateNetSavingsForPeriod(
            @Param("user") UserEntity user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Checks if any transaction references the given category.
     * Used as a guard before allowing category deletion.
     *
     * @param category The category to check references for.
     * @return true if at least one transaction references the category, false otherwise.
     */
    boolean existsByCategory(CategoryEntity category);
}

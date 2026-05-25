package com.rudra.financemanager.repositories;

import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link CategoryEntity}.
 * Houses query methods for checking, fetching, and managing user custom and system default categories.
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    /**
     * Finds all system default categories (where user is null).
     *
     * @return List of default category entities.
     */
    List<CategoryEntity> findByUserIsNull();

    /**
     * Finds all custom categories created by a specific user.
     *
     * @param user The owner of the custom categories.
     * @return List of custom category entities.
     */
    List<CategoryEntity> findByUser(UserEntity user);

    /**
     * Finds all categories accessible to the specified user.
     * This includes system defaults (user is null) and the user's custom categories.
     * Results are ordered by category type and name.
     *
     * @param user The user accessing the categories.
     * @return List of all accessible category entities.
     */
    @Query("SELECT c FROM CategoryEntity c WHERE c.user IS NULL OR c.user = :user ORDER BY c.type ASC, c.name ASC")
    List<CategoryEntity> findAllAccessibleCategories(@Param("user") UserEntity user);

    /**
     * Checks if a category with the specified name exists (case-insensitive) either as a system default
     * or as a custom category owned by the given user.
     *
     * @param name The category name to search.
     * @param user The user to check custom categories for.
     * @return true if the category exists, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CategoryEntity c " +
            "WHERE LOWER(c.name) = LOWER(:name) AND (c.user IS NULL OR c.user = :user)")
    boolean existsByAccessibleName(@Param("name") String name, @Param("user") UserEntity user);

    /**
     * Retrieves an accessible category by its name (case-insensitive).
     * Searches both system default categories and the user's custom categories.
     *
     * @param name The category name.
     * @param user The user checking accessibility.
     * @return Optional containing the CategoryEntity if found.
     */
    @Query("SELECT c FROM CategoryEntity c WHERE LOWER(c.name) = LOWER(:name) AND (c.user IS NULL OR c.user = :user)")
    Optional<CategoryEntity> findAccessibleCategoryByName(@Param("name") String name, @Param("user") UserEntity user);
}

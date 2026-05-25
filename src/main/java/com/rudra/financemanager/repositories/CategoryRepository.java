package com.rudra.financemanager.repositories;

import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByUserIsNull();

    List<CategoryEntity> findByUser(UserEntity user);

    @Query("SELECT c FROM CategoryEntity c WHERE c.user IS NULL OR c.user = :user ORDER BY c.type ASC, c.name ASC")
    List<CategoryEntity> findAllAccessibleCategories(@Param("user") UserEntity user);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CategoryEntity c " +
            "WHERE LOWER(c.name) = LOWER(:name) AND (c.user IS NULL OR c.user = :user)")
    boolean existsByAccessibleName(@Param("name") String name, @Param("user") UserEntity user);

    @Query("SELECT c FROM CategoryEntity c WHERE LOWER(c.name) = LOWER(:name) AND (c.user IS NULL OR c.user = :user)")
    Optional<CategoryEntity> findAccessibleCategoryByName(@Param("name") String name, @Param("user") UserEntity user);
}



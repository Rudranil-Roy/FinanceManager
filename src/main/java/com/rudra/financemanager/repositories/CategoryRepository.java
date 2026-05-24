package com.rudra.financemanager.repositories;

import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByUserIsNull();

    List<CategoryEntity> findByUser(UserEntity user);

    List<CategoryEntity> findByUserOrUserIsNull(UserEntity user, UserEntity nullUser);

    Optional<CategoryEntity> findByNameAndUser(String name, UserEntity user);

    Optional<CategoryEntity> findByNameAndUserIsNull(String name);

    boolean existsByNameAndUser(String name, UserEntity user);

    boolean existsByNameAndUserIsNull(String name);

    boolean existsByNameAndUserOrUserIsNull(String name, UserEntity user, UserEntity nullUser);

    List<CategoryEntity> findByTypeAndUserOrTypeAndUserIsNull(TransactionTypeEnum type1, UserEntity user, TransactionTypeEnum type2, UserEntity nullUser);

}


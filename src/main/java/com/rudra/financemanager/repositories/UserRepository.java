package com.rudra.financemanager.repositories;

import com.rudra.financemanager.entities.UserEntitiy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntitiy, Long> {

    Optional<UserEntitiy> findByUsername(String username);

    boolean existsByUsername(String username);
}

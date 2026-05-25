package com.rudra.financemanager.repositories;

import com.rudra.financemanager.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link UserEntity}.
 * Houses query methods to retrieve or check users by username (email).
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Retrieves a user entity by their username (email address).
     *
     * @param username The email address/username.
     * @return Optional containing the UserEntity if found.
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Checks if a user already exists with the specified username (email address).
     *
     * @param username The email address/username to verify.
     * @return true if the username is taken, false otherwise.
     */
    boolean existsByUsername(String username);
}

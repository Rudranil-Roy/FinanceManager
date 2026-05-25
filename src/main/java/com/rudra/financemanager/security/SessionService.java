package com.rudra.financemanager.security;

import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.UnauthorizedException;
import com.rudra.financemanager.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service to manage active session operations and fetch authenticated user context.
 * Resolves context directly from Spring's SecurityContextHolder.
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserRepository userRepository;

    /**
     * Resolves and retrieves the currently authenticated user from the database.
     * Throws an exception if the user is unauthenticated or not found in the DB.
     *
     * @return The currently authenticated UserEntity.
     * @throws UnauthorizedException if the user is not authenticated or not found in the database.
     */
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
    }

    /**
     * Retrieves the database ID of the currently authenticated user.
     *
     * @return The user's ID.
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Retrieves the email/username of the currently authenticated user.
     *
     * @return The user's username string.
     */
    public String getCurrentUserUsername() {
        return getCurrentUser().getUsername();
    }

    /**
     * Programmatically invalidates the HTTP session and clears the security context.
     *
     * @param request The current servlet request.
     */
    public void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}

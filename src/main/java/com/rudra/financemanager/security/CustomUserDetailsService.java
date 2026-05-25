package com.rudra.financemanager.security;

import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 * Responsible for loading user credentials from the database for authentication purposes.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by their unique email/username.
     *
     * @param username The email address identifying the user.
     * @return UserDetails representation for Spring Security.
     * @throws UsernameNotFoundException if the user does not exist in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomerUserDetails(userEntity);
    }
}

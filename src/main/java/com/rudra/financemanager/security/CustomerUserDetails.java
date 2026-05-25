package com.rudra.financemanager.security;

import com.rudra.financemanager.entities.UserEntity;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of Spring Security's {@link UserDetails} interface.
 * Wraps the application's {@link UserEntity} to bridge security and domain layers.
 */
@RequiredArgsConstructor
public class CustomerUserDetails implements UserDetails {

    private final UserEntity userEntity;

    /**
     * Gets the unique database ID of the user.
     *
     * @return User identifier.
     */
    public Long getId() {
        return userEntity.getId();
    }

    /**
     * Retrieves the authorities granted to the user.
     * Defaults to single role "ROLE_USER" for all system users.
     *
     * @return Collection of GrantedAuthority instances.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Gets the hashed password used to authenticate the user.
     *
     * @return Hashed password string.
     */
    @Override
    public @Nullable String getPassword() {
        return userEntity.getPassword();
    }

    /**
     * Gets the email address used as the login username.
     *
     * @return Email username.
     */
    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    /**
     * Checks if the user's account has expired.
     *
     * @return true if the account is valid (non-expired), false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Checks if the user is locked or unlocked.
     *
     * @return true if the user is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * Checks if the user's credentials (password) have expired.
     *
     * @return true if credentials are valid (non-expired), false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * Checks if the user is enabled or disabled.
     *
     * @return true if enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

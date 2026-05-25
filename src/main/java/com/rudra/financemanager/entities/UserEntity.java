package com.rudra.financemanager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a user in the database.
 * Houses login credentials, contact details, and maintains cascading relationships to the
 * user's transactions, custom categories, and savings goals.
 */
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique email address used as the login username.
     */
    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String username;

    /**
     * Hashed user password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Full name of the user.
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * User's contact phone number.
     */
    @Column(nullable = false)
    private String phoneNumber;

    /**
     * List of all transactions belonging to the user.
     */
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionEntity> transactions = new ArrayList<>();

    /**
     * List of custom categories created by the user.
     */
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryEntity> categories = new ArrayList<>();

    /**
     * List of savings goals created by the user.
     */
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsGoalEntity> goals = new ArrayList<>();
}

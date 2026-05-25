package com.rudra.financemanager.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing a category in the database.
 * A category can be a standard system default or a custom category defined by a specific user.
 * Supports a relationship to the {@link UserEntity} who created it.
 */
@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryEntity {

    /**
     * Unique identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the category (e.g. "Rent", "Groceries").
     */
    @Column(nullable = false)
    private String name;

    /**
     * The transaction type this category belongs to (INCOME or EXPENSE).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionTypeEnum type;

    /**
     * Flag indicating whether this is a user-defined custom category.
     */
    @Column(nullable = false)
    private boolean isCustom;

    /**
     * The user who created this custom category. Null for system default categories.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * Verifies if two CategoryEntity instances are equal based on their database identifier.
     *
     * @param o Object to compare with.
     * @return true if the ID matches, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    /**
     * Computes the hash code of the CategoryEntity class.
     *
     * @return the class hash code.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

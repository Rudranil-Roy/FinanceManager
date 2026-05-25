package com.rudra.financemanager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA entity representing a transaction in the database.
 * Stores information about amount, date, description, category, and target user who owns it.
 */
@Entity
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransactionEntity {

    /**
     * Unique identifier for the transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The monetary amount of the transaction.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /**
     * The date of the transaction.
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Optional descriptive text or notes.
     */
    private String description;

    /**
     * The category this transaction belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    /**
     * The user who owns this transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Verifies if two TransactionEntity instances are equal based on their database identifier.
     *
     * @param o Object to compare with.
     * @return true if the ID matches, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    /**
     * Computes the hash code of the TransactionEntity class.
     *
     * @return the class hash code.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

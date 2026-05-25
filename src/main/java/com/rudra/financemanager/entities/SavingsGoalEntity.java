package com.rudra.financemanager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA entity representing a savings goal in the database.
 * Tracks target monetary goals, deadlines, starting dates, and links to the associated {@link UserEntity}.
 */
@Entity
@Table(name = "savingsGoals")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SavingsGoalEntity {

    /**
     * Unique identifier for the savings goal.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name/title of the savings goal.
     */
    @Column(nullable = false)
    private String goalName;

    /**
     * The target monetary amount to achieve.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    /**
     * The target deadline date.
     */
    @Column(nullable = false)
    private LocalDate targetDate;

    /**
     * The start date when the goal tracking begins.
     */
    @Column(nullable = false)
    private LocalDate startDate;

    /**
     * The user who owns this savings goal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Verifies if two SavingsGoalEntity instances are equal based on their database identifier.
     *
     * @param o Object to compare with.
     * @return true if the ID matches, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavingsGoalEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    /**
     * Computes the hash code of the SavingsGoalEntity class.
     *
     * @return the class hash code.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

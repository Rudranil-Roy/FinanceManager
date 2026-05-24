package com.rudra.financemanager.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum type;

    private boolean isCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "user_id")
    private UserEntity user;
}

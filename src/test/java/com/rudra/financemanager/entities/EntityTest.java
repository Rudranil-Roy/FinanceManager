package com.rudra.financemanager.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EntityCoverageTest {

    @Test
    void testUserEntity() {
        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .username("test@example.com")
                .password("pass")
                .fullName("John Doe")
                .phoneNumber("1234567890")
                .build();

        assertEquals(1L, user1.getId());
        assertEquals("test@example.com", user1.getUsername());
        assertEquals("pass", user1.getPassword());
        assertEquals("John Doe", user1.getFullName());
        assertEquals("1234567890", user1.getPhoneNumber());
        assertNotNull(user1.getTransactions());
        assertNotNull(user1.getCategories());
        assertNotNull(user1.getGoals());

        UserEntity user2 = new UserEntity();
        user2.setUsername("test@example.com");

        UserEntity user3 = new UserEntity();
        user3.setUsername("different@example.com");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        assertNotEquals(user1, user3);

        assertEquals(user1, user1);
        assertNotEquals(user1, null);
        assertNotEquals(user1, new Object());
    }

    @Test
    void testCategoryEntity() {
        CategoryEntity cat1 = CategoryEntity.builder()
                .id(1L)
                .name("Food")
                .type(TransactionTypeEnum.EXPENSE)
                .isCustom(true)
                .build();

        assertEquals(1L, cat1.getId());
        assertEquals("Food", cat1.getName());
        assertEquals(TransactionTypeEnum.EXPENSE, cat1.getType());
        assertTrue(cat1.isCustom());

        CategoryEntity cat2 = new CategoryEntity();
        cat2.setId(1L);

        CategoryEntity cat3 = new CategoryEntity();
        cat3.setId(2L);

        CategoryEntity unsaved1 = new CategoryEntity();
        CategoryEntity unsaved2 = new CategoryEntity();

        assertEquals(cat1, cat2);

        assertNotEquals(cat1, cat3);

        assertNotEquals(unsaved1, unsaved2);

        assertNotEquals(cat1, unsaved1);

        assertEquals(cat1, cat1);
        assertNotEquals(cat1, null);
        assertNotEquals(cat1, new Object());

        assertEquals(cat1.hashCode(), unsaved1.hashCode());
    }

    @Test
    void testTransactionEntity() {
        TransactionEntity tx1 = TransactionEntity.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .date(LocalDate.now())
                .description("Test")
                .build();

        assertEquals(1L, tx1.getId());
        assertEquals(new BigDecimal("100.00"), tx1.getAmount());
        assertNotNull(tx1.getDate());
        assertEquals("Test", tx1.getDescription());

        TransactionEntity tx2 = new TransactionEntity();
        tx2.setId(1L);

        TransactionEntity tx3 = new TransactionEntity();
        tx3.setId(2L);

        TransactionEntity unsaved1 = new TransactionEntity();
        TransactionEntity unsaved2 = new TransactionEntity();

        assertEquals(tx1, tx2);

        assertNotEquals(tx1, tx3);

        assertNotEquals(unsaved1, unsaved2);

        assertEquals(tx1, tx1);
        assertNotEquals(tx1, null);
        assertNotEquals(tx1, new Object());

        assertEquals(tx1.hashCode(), unsaved1.hashCode());
    }

    @Test
    void testSavingsGoalEntity() {
        SavingsGoalEntity goal1 = SavingsGoalEntity.builder()
                .id(1L)
                .goalName("Car")
                .targetAmount(new BigDecimal("5000.00"))
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusYears(1))
                .build();

        assertEquals(1L, goal1.getId());
        assertEquals("Car", goal1.getGoalName());
        assertEquals(new BigDecimal("5000.00"), goal1.getTargetAmount());
        assertNotNull(goal1.getStartDate());
        assertNotNull(goal1.getTargetDate());

        SavingsGoalEntity goal2 = new SavingsGoalEntity();
        goal2.setId(1L);

        SavingsGoalEntity goal3 = new SavingsGoalEntity();
        goal3.setId(2L);

        SavingsGoalEntity unsaved1 = new SavingsGoalEntity();
        SavingsGoalEntity unsaved2 = new SavingsGoalEntity();

        assertEquals(goal1, goal2);

        assertNotEquals(goal1, goal3);

        assertNotEquals(unsaved1, unsaved2);

        assertEquals(goal1, goal1);
        assertNotEquals(goal1, null);
        assertNotEquals(goal1, new Object());

        assertEquals(goal1.hashCode(), unsaved1.hashCode());
    }
}
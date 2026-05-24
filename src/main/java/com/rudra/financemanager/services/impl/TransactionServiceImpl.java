package com.rudra.financemanager.services.impl;

import com.rudra.financemanager.dto.transaction.CreateTransactionRequest;
import com.rudra.financemanager.dto.transaction.TransactionResponse;
import com.rudra.financemanager.dto.transaction.UpdateTransactionRequest;
import com.rudra.financemanager.entities.CategoryEntity;
import com.rudra.financemanager.entities.TransactionEntity;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.BadRequestException;
import com.rudra.financemanager.exceptions.ForbiddenException;
import com.rudra.financemanager.exceptions.ResourceNotFoundException;
import com.rudra.financemanager.repositories.CategoryRepository;
import com.rudra.financemanager.repositories.TransactionRepository;
import com.rudra.financemanager.security.SessionService;
import com.rudra.financemanager.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final SessionService sessionService;


    @Override
    @Transactional
    public TransactionResponse create(CreateTransactionRequest createTransactionRequest) {
        UserEntity currentUser = sessionService.getCurrentUser();
        CategoryEntity category =  findAccessibleCategoryByName(createTransactionRequest.getCategory(), currentUser);

        if(createTransactionRequest.getDate() == null){
            throw new BadRequestException("Date is required");
        }

        if(createTransactionRequest.getDate().isAfter(LocalDate.now())){
            throw new BadRequestException("Date cannot be in the future");
        }

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .amount(createTransactionRequest.getAmount())
                .date(createTransactionRequest.getDate())
                .description(createTransactionRequest.getDescription())
                .category(category)
                .user(currentUser).build();

        TransactionEntity savedTransactionEntity = transactionRepository.save(transactionEntity);
        return toResponse(savedTransactionEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAll(LocalDate startDate, LocalDate endDate, Long categoryId) {
        UserEntity currentUser = sessionService.getCurrentUser();

        CategoryEntity filterCategory;
        if (categoryId != null) {
            filterCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            if(!isAccessibleCategory(filterCategory, currentUser)){
                throw new ResourceNotFoundException("Category not found");
            }
        } else {
            filterCategory = null;
        }

        return transactionRepository.findByUserOrderByDateDesc(currentUser)
                .stream()
                .filter(tx -> startDate == null || !tx.getDate().isBefore(startDate))
                .filter(tx -> endDate == null || !tx.getDate().isAfter(endDate))
                .filter(tx -> filterCategory == null || tx.getCategory().getId().equals(filterCategory.getId()))
                .sorted(Comparator.comparing(TransactionEntity::getDate).reversed()
                        .thenComparing(TransactionEntity::getId, Comparator.reverseOrder()))
                .map(this::toResponse)
                .toList();

    }

    @Override
    public TransactionResponse update(Long id, UpdateTransactionRequest updateTransactionRequest) {
        UserEntity currentUser = sessionService.getCurrentUser();

        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access denied to this transaction");
        }

        if (updateTransactionRequest.getAmount() != null) {
            transaction.setAmount(updateTransactionRequest.getAmount());
        }

        if (updateTransactionRequest.getDescription() != null) {
            transaction.setDescription(updateTransactionRequest.getDescription());
        }

        if (updateTransactionRequest.getCategory() != null && !updateTransactionRequest.getCategory().isBlank()) {
            CategoryEntity newCategory = findAccessibleCategoryByName(updateTransactionRequest.getCategory(), currentUser);
            transaction.setCategory(newCategory);
        }

        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        return toResponse(savedTransaction);
    }

    @Override
    public void delete(Long id) {
        UserEntity currentUser = sessionService.getCurrentUser();

        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access denied to this transaction");
        }

        transactionRepository.delete(transaction);
    }

    private CategoryEntity findAccessibleCategoryByName(String categoryName, UserEntity currentUser) {
        if(categoryName == null || categoryName.isBlank()){
            throw new BadRequestException("Category name is required");
        }

        return categoryRepository.findByUserIsNull().stream()
                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                .findFirst().orElseGet(() -> categoryRepository.findByUser(currentUser).stream()
                        .filter(c-> c.getName().equalsIgnoreCase(categoryName))
                        .findFirst().orElseThrow(() -> new ResourceNotFoundException("Category not found")));
    }

    private boolean isAccessibleCategory(CategoryEntity category, UserEntity currentUser) {
        return category.getUser() == null || category.getUser().getId().equals(currentUser.getId());
    }

    private TransactionResponse toResponse(TransactionEntity transaction) {
        TransactionTypeEnum type = transaction.getCategory().getType() == TransactionTypeEnum.INCOME
                ? TransactionTypeEnum.INCOME
                : TransactionTypeEnum.EXPENSE;

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getCategory().getName(),
                transaction.getDescription(),
                type
        );
    }
}

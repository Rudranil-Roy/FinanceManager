package com.rudra.financemanager.services.impl;

import com.rudra.financemanager.dto.goal.CreateGoalRequest;
import com.rudra.financemanager.dto.goal.GoalResponse;
import com.rudra.financemanager.dto.goal.UpdateGoalRequest;
import com.rudra.financemanager.entities.SavingsGoalEntity;
import com.rudra.financemanager.entities.TransactionEntity;
import com.rudra.financemanager.entities.TransactionTypeEnum;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.BadRequestException;
import com.rudra.financemanager.exceptions.ForbiddenException;
import com.rudra.financemanager.exceptions.ResourceNotFoundException;
import com.rudra.financemanager.repositories.SavingsGoalRepository;
import com.rudra.financemanager.repositories.TransactionRepository;
import com.rudra.financemanager.security.SessionService;
import com.rudra.financemanager.services.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;
    private final SessionService sessionService;
    private final Clock clock;

    @Override
    @Transactional
    public GoalResponse create(CreateGoalRequest request) {
        UserEntity currentUser = sessionService.getCurrentUser();

        if (request.getGoalName() == null || request.getGoalName().isBlank()) {
            throw new BadRequestException("Goal name is required");
        }
        if (request.getTargetAmount() == null || request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Target amount must be positive");
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : today;

        if (startDate.isAfter(today)) {
            throw new BadRequestException("Start date cannot be in the future");
        }
        if (request.getTargetDate() == null || !request.getTargetDate().isAfter(today)) {
            throw new BadRequestException("Target date must be in the future");
        }

        SavingsGoalEntity goal = SavingsGoalEntity.builder()
                .goalName(request.getGoalName())
                .targetAmount(request.getTargetAmount())
                .startDate(request.getStartDate())
                .targetDate(request.getTargetDate())
                .user(currentUser).build();

        SavingsGoalEntity saved = savingsGoalRepository.save(goal);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> getAll() {
        UserEntity currentUser = sessionService.getCurrentUser();

        return savingsGoalRepository.findByUser(currentUser).stream()
                .sorted(Comparator.comparing(SavingsGoalEntity::getId).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GoalResponse getById(Long id) {
        SavingsGoalEntity goal = getOwnedGoal(id);
        return toResponse(goal);
    }

    @Override
    @Transactional
    public GoalResponse update(Long id, UpdateGoalRequest request) {
        SavingsGoalEntity goal = getOwnedGoal(id);
        LocalDate today = LocalDate.now(clock);

        if (request.getTargetAmount() != null) {
            if (request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Target amount must be positive");
            }
            goal.setTargetAmount(request.getTargetAmount());
        }

        if (request.getTargetDate() != null) {
            if (!request.getTargetDate().isAfter(today)) {
                throw new BadRequestException("Target date must be in the future");
            }
            goal.setTargetDate(request.getTargetDate());
        }

        SavingsGoalEntity saved = savingsGoalRepository.save(goal);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SavingsGoalEntity goal = getOwnedGoal(id);
        savingsGoalRepository.delete(goal);
    }

    private SavingsGoalEntity getOwnedGoal(Long id) {
        UserEntity currentUser = sessionService.getCurrentUser();

        SavingsGoalEntity goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access denied to this goal");
        }

        return goal;
    }

    private GoalResponse toResponse(SavingsGoalEntity goal) {
        BigDecimal currentProgress = calculateCurrentProgress(goal);
        BigDecimal effectiveProgress = currentProgress.compareTo(BigDecimal.ZERO) > 0
                ? currentProgress
                : BigDecimal.ZERO;

        BigDecimal remainingAmount = goal.getTargetAmount().subtract(effectiveProgress);
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }

        BigDecimal progressPercentage = BigDecimal.ZERO;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progressPercentage = effectiveProgress
                    .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return new GoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getStartDate(),
                currentProgress.setScale(2, RoundingMode.HALF_UP),
                progressPercentage,
                remainingAmount.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal calculateCurrentProgress(SavingsGoalEntity goal) {
        LocalDate today = LocalDate.now(clock);

        List<TransactionEntity> transactions = transactionRepository
                .findByUserAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
                        goal.getUser(),
                        goal.getStartDate(),
                        today
                );

        BigDecimal total = BigDecimal.ZERO;

        for (TransactionEntity transaction : transactions) {
            if (transaction.getCategory().getType() == TransactionTypeEnum.INCOME) {
                total = total.add(transaction.getAmount());
            } else {
                total = total.subtract(transaction.getAmount());
            }
        }

        return total;
    }

}

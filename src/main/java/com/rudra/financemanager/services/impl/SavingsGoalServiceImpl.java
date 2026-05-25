package com.rudra.financemanager.services.impl;

import com.rudra.financemanager.dto.goal.CreateGoalRequest;
import com.rudra.financemanager.dto.goal.GoalResponse;
import com.rudra.financemanager.dto.goal.UpdateGoalRequest;
import com.rudra.financemanager.entities.SavingsGoalEntity;
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
import java.util.List;

/**
 * Implementation of {@link SavingsGoalService}.
 * Executes business operations to manage user savings targets and dynamically calculate savings
 * progress percentages based on historical transaction net savings.
 */
@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;
    private final SessionService sessionService;
    private final Clock clock;

    /**
     * Creates a new savings goal for the current authenticated user.
     * Enforces start dates cannot be in the future, target dates must be in the future,
     * and target amounts must be positive.
     *
     * @param request DTO containing parameters for the new savings goal.
     * @return GoalResponse detailed DTO.
     * @throws BadRequestException if parameter validations fail.
     */
    @Override
    @Transactional
    public GoalResponse create(final CreateGoalRequest request) {
        final UserEntity currentUser = sessionService.getCurrentUser();

        if (request.getGoalName() == null || request.getGoalName().isBlank()) {
            throw new BadRequestException("Goal name is required");
        }
        if (request.getTargetAmount() == null || request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Target amount must be positive");
        }

        final LocalDate today = LocalDate.now(clock);
        final LocalDate effectiveStartDate = request.getStartDate() != null ? request.getStartDate() : today;

        if (effectiveStartDate.isAfter(today)) {
            throw new BadRequestException("Start date cannot be in the future");
        }
        if (request.getTargetDate() == null || !request.getTargetDate().isAfter(today)) {
            throw new BadRequestException("Target date must be in the future");
        }

        final SavingsGoalEntity goal = SavingsGoalEntity.builder()
                .goalName(request.getGoalName().trim())
                .targetAmount(request.getTargetAmount())
                .startDate(effectiveStartDate)
                .targetDate(request.getTargetDate())
                .user(currentUser)
                .build();

        return toResponse(savingsGoalRepository.save(goal));
    }

    /**
     * Retrieves all savings goals belonging to the current user, ordered by ID descending.
     *
     * @return List of GoalResponse DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> getAll() {
        final UserEntity currentUser = sessionService.getCurrentUser();

        return savingsGoalRepository.findByUserOrderByIdDesc(currentUser).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Retrieves details of a specific savings goal by its unique ID.
     * Validates that the goal belongs to the current user.
     *
     * @param id The identifier of the savings goal.
     * @return GoalResponse detailed DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public GoalResponse getById(final Long id) {
        return toResponse(getOwnedGoal(id));
    }

    /**
     * Updates an existing savings goal's target amount and/or target deadline date.
     *
     * @param id      The identifier of the savings goal to update.
     * @param request DTO containing updated goal parameters.
     * @return GoalResponse detailed DTO.
     * @throws BadRequestException if parameter updates are invalid (e.g. negative amount).
     */
    @Override
    @Transactional
    public GoalResponse update(final Long id, final UpdateGoalRequest request) {
        final SavingsGoalEntity goal = getOwnedGoal(id);
        final LocalDate today = LocalDate.now(clock);

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

        return toResponse(savingsGoalRepository.save(goal));
    }

    /**
     * Deletes a savings goal by its unique ID.
     *
     * @param id The identifier of the savings goal to delete.
     */
    @Override
    @Transactional
    public void delete(final Long id) {
        final SavingsGoalEntity goal = getOwnedGoal(id);
        savingsGoalRepository.delete(goal);
    }

    /**
     * Retrieves a savings goal by ID, asserting ownership by the current user.
     *
     * @param id The savings goal ID.
     * @return The verified SavingsGoalEntity.
     * @throws ResourceNotFoundException if the goal is not found.
     * @throws ForbiddenException        if the user does not own the goal.
     */
    private SavingsGoalEntity getOwnedGoal(final Long id) {
        final UserEntity currentUser = sessionService.getCurrentUser();

        final SavingsGoalEntity goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access denied to this goal");
        }

        return goal;
    }

    /**
     * Maps a {@link SavingsGoalEntity} to {@link GoalResponse}, calculating progress.
     * Computes the remaining amount and percentage from the target amount and progress savings.
     *
     * @param goal The savings goal.
     * @return Constructed GoalResponse.
     */
    private GoalResponse toResponse(final SavingsGoalEntity goal) {
        final BigDecimal currentProgress = calculateCurrentProgress(goal);

        final BigDecimal effectiveProgress = currentProgress.compareTo(BigDecimal.ZERO) > 0
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

    /**
     * Computes a savings goal's progress by calculating net savings in the database.
     *
     * @param goal The savings goal.
     * @return Summed net savings.
     */
    private BigDecimal calculateCurrentProgress(final SavingsGoalEntity goal) {
        final LocalDate today = LocalDate.now(clock);

        final BigDecimal netSavings = transactionRepository.calculateNetSavingsForPeriod(
                goal.getUser(),
                goal.getStartDate(),
                today
            );

        return netSavings != null ? netSavings : BigDecimal.ZERO;
    }
}
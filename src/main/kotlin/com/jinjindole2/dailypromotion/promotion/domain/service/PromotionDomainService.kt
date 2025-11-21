package com.jinjindole2.dailypromotion.promotion.domain.service

import com.jinjindole2.dailypromotion.promotion.domain.model.Promotion
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinType
import com.jinjindole2.dailypromotion.promotion.domain.model.JoinType
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * 헥사고날 아키텍처 개선: 도메인 서비스 추가
 * - 복잡한 비즈니스 규칙을 도메인 계층에서 처리
 * - 여러 도메인 객체 간의 협력이 필요한 로직
 */
@Service
class PromotionDomainService {

    /**
     * 프로모션 참여 가능 여부 검증
     * - 도메인 규칙을 하나의 메서드로 응집
     */
    fun validateJoinEligibility(
        promotion: Promotion,
        userId: Long,
        joinDate: LocalDate,
        joinTypes: List<PromotionJoinType>,
        userJoinHistories: List<PromotionJoinHistory>,
        hasJoinedPrerequisiteAd: Boolean = true
    ): JoinValidationResult {

        // 1. 기본 참여 조건 검증
        if (!promotion.isWithinJoinPeriod(joinDate)) {
            return JoinValidationResult.failure("참여 가능한 일자가 아닙니다.")
        }

        if (!promotion.hasJoinCountAvailable()) {
            return JoinValidationResult.failure("참가 불가능한 프로모션 (프로모션 참가횟수 초과)")
        }

        // 2. 이미 참여한 프로모션인지 확인
        val alreadyJoined = userJoinHistories.any {
            it.promoId == promotion.id && it.userId == userId && it.joinDate == joinDate
        }
        if (alreadyJoined) {
            return JoinValidationResult.failure("이미 참여한 프로모션 입니다.")
        }

        // 3. 특별 참여 제한 조건 검증
        for (joinType in joinTypes) {
            when (joinType.joinType) {
                JoinType.N_DUP_LIMIT -> {
                    val userJoinCount = userJoinHistories.count { it.promoId == promotion.id && it.userId == userId }
                    if (userJoinCount >= (joinType.limitDupJoinCount ?: 0)) {
                        return JoinValidationResult.failure("참가 불가능한 프로모션 (개인 참가횟수 초과)")
                    }
                }

                JoinType.LEADING -> {
                    if (!hasJoinedPrerequisiteAd) {
                        return JoinValidationResult.failure("참가 불가능한 프로모션 (선행프로모션 참가 필요)")
                    }
                }

            }
        }

        return JoinValidationResult.success()
    }

    /**
     * 프로모션 제목 중복 검증
     */
    fun validateUniqueTitle(title: String, existingTitles: List<String>): Boolean {
        return !existingTitles.contains(title)
    }

    /**
     * 프로모션 등록 시 비즈니스 규칙 검증
     */
    fun validatePromotionCreation(promotion: Promotion): ValidationResult {
        val errors = mutableListOf<String>()

        if (promotion.title.isBlank()) {
            errors.add("프로모션명은 필수입니다.")
        }

        if (promotion.rewardAmount <= 0) {
            errors.add("적립 금액은 0보다 커야 합니다.")
        }

        if (promotion.limitJoinCount <= 0) {
            errors.add("참여 가능 횟수는 0보다 커야 합니다.")
        }

        if (promotion.startDate.isAfter(promotion.endDate)) {
            errors.add("시작일은 종료일보다 늦을 수 없습니다.")
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }
}

/**
 * 참여 검증 결과
 */
sealed class JoinValidationResult {
    object Success : JoinValidationResult()
    data class Failure(val reason: String) : JoinValidationResult()

    companion object {
        fun success() = Success
        fun failure(reason: String) = Failure(reason)
    }

    fun isSuccess(): Boolean = this is Success
    fun getFailureReason(): String? = (this as? Failure)?.reason
}

/**
 * 일반 검증 결과
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val errors: List<String>) : ValidationResult()

    companion object {
        fun success() = Success
        fun failure(errors: List<String>) = Failure(errors)
    }

    fun isSuccess(): Boolean = this is Success
}
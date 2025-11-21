package com.jinjindole2.dailypromotion.promotion.domain.event

import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinHistory
import java.time.Instant
import java.time.LocalDate

data class JoinPromotionEvent(
    // 정보
    val promoId: Long,
    val userId: Long,
    val joinDate: LocalDate,
    val rewardAmount: Long,

    // 공통 메타 데이터 (DomainEvent에서 상속)
    override val eventId: String = java.util.UUID.randomUUID().toString(),
    override val producedAt: Instant = Instant.now(),
    override val traceId: String? = null
) : DomainEvent(
    eventId = eventId,
    eventType = "PROMOTION_JOIN",
    producedAt = producedAt,
    traceId = traceId
) {
    fun toDomain() = PromotionJoinHistory(
        promoId = this.promoId,
        userId = this.userId,
        joinDate = this.joinDate,
        rewardAmount = this.rewardAmount
    )
}
package com.jinjindole2.jinpro.promotion.domain.event

import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import java.time.Instant
import java.time.LocalDate

data class RegisterPromotionEvent (
    //  Promotion 정보
    val id : Long? = null,
    var title : String,
    var rewardAmount : Long,
    var leftJoinCount : Long,
    var limitJoinCount : Long,
    var content : String?,
    var imageUrl : String?,
    var startDate : LocalDate,
    var endDate : LocalDate,

    // 공통 메타 데이터 (DomainEvent에서 상속)
    override val eventId: String = java.util.UUID.randomUUID().toString(),
    override val producedAt: Instant = Instant.now(),
    override val traceId: String? = null
) : DomainEvent(
    eventId = eventId,
    eventType = "PROMOTION_REGISTER",
    producedAt = producedAt,
    traceId = traceId
) {
    companion object {
        fun fromPromotion(adv: Promotion, traceId: String?) : RegisterPromotionEvent{
            return RegisterPromotionEvent(
                id = adv.id,
                title = adv.title,
                rewardAmount = adv.rewardAmount,
                leftJoinCount = adv.leftJoinCount,
                limitJoinCount = adv.limitJoinCount,
                content = adv.content,
                imageUrl = adv.imageUrl,
                startDate = adv.startDate,
                endDate = adv.endDate,
                traceId = traceId
            )
        }
    }

    fun toDomain() = Promotion(
        id = this.id,
        title = this.title,
        rewardAmount = this.rewardAmount,
        leftJoinCount = this.leftJoinCount,
        limitJoinCount = this.limitJoinCount,
        content = this.content,
        imageUrl = this.imageUrl,
        startDate = this.startDate,
        endDate = this.endDate,
    )
}
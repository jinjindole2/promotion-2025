package com.jinjindole2.dailypromotion.promotion.domain.event

import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinType
import com.jinjindole2.dailypromotion.promotion.domain.model.JoinType
import java.time.Instant

data class AddPromotionJoinTypeEvent (
    //  Promotion 정보
    val promoId : Long,
    val joinType: JoinType,
    val limitDupJoinCount: Long? = null,
    val leadPromoIdList : List<Long>? = null,

    // 공통 메타 데이터 (DomainEvent에서 상속)
    override val eventId: String = java.util.UUID.randomUUID().toString(),
    override val producedAt: Instant = Instant.now(),
    override val traceId: String? = null
) : DomainEvent(
    eventId = eventId,
    eventType = "PROMOTION_ADD_JOIN_TYPE",
    producedAt = producedAt,
    traceId = traceId
) {
    companion object {
        fun fromPromotionJoinType(adv: PromotionJoinType, traceId: String?) : AddPromotionJoinTypeEvent{
            return AddPromotionJoinTypeEvent(
                promoId = adv.promoId,
                joinType = adv.joinType,
                limitDupJoinCount = adv.limitDupJoinCount,
                leadPromoIdList = adv.leadPromoIdList,
                traceId = traceId
            )
        }
    }

    fun toDomain() = PromotionJoinType(
        promoId = this.promoId,
        joinType = this.joinType,
        limitDupJoinCount = this.limitDupJoinCount,
        leadPromoIdList = this.leadPromoIdList
    )
}
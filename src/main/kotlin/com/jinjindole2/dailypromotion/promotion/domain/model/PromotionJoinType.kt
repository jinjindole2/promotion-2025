package com.jinjindole2.dailypromotion.promotion.domain.model

import java.time.LocalDateTime

class PromotionJoinType(
    val promoId : Long,
    val joinType: JoinType,
    val limitDupJoinCount: Long? = null,
    val leadPromoIdList: List<Long>? = null,
    var createdDate : LocalDateTime? = null,
    var lastModifiedDate : LocalDateTime? = null,
)

enum class JoinType {
    LEADING,        // 특정프로모션 ID를 참가한 이력이 있는 유저
    N_DUP_LIMIT     // N번 이상 참가한 유저는 제한
}

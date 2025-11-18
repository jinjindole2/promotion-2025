package com.jinjindole2.jinpro.promotion.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

class PromotionJoinHistory(
    val id : Long? = null,
    val promoId : Long,
    val userId : Long,
    val joinDate : LocalDate,
    val rewardAmount: Long,
    var createdDate : LocalDateTime? = null,
    var lastModifiedDate : LocalDateTime? = null,
)
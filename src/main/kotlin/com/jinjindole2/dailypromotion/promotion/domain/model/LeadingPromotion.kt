package com.jinjindole2.dailypromotion.promotion.domain.model

import java.time.LocalDateTime

class LeadingPromotion(
    val leadPromoId : Long,
    val trailPromoId : Long,
    var createdDate : LocalDateTime? = null,
    var lastModifiedDate : LocalDateTime? = null,
)
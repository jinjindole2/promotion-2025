package com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto

import jakarta.validation.constraints.NotNull

data class JoinPromotionReqDto (
    @field:NotNull(message = "프로모션ID가 존재하지 않습니다.")
    val promoId : Long,

    @field:NotNull(message = "유저ID가 존재하지 않습니다.")
    val userId : Long,
)
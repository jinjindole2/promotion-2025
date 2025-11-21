package com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto

import com.jinjindole2.dailypromotion.promotion.domain.model.JoinType
import jakarta.validation.constraints.NotNull

data class AddPromotionJoinTypeReqDto (
    @field:NotNull(message = "프로모션ID가 존재하지 않습니다.")
    val promoId : Long,

    @field:NotNull(message = "유저ID가 존재하지 않습니다.")
    val joinType : JoinType,

    // JoinType 에 따라 필요
    val limitDupJoinCount : Long?,

    // JoinType 에 따라 필요
    val leadPromoIdList: List<Long>?,
)
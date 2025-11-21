package com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto

import java.time.LocalDate

data class FindPromotionJoinHistoryResDto (
    val promoId : Long,
    val userId : Long,
    val joinDate : LocalDate,
    var title : String,
    var rewardAmount : Long,
)
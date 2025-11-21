package com.jinjindole2.dailypromotion.promotion.application.port.`in`.web

import com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto.FindPromotionResDto
import com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto.PaginatedRes

interface FindJoinablePromotionUseCase {
    suspend fun findJoinablePromotion(userId: Long, page: Long, size: Long): PaginatedRes<FindPromotionResDto>
}
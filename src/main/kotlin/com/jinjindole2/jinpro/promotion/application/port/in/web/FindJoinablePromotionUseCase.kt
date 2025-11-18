package com.jinjindole2.jinpro.promotion.application.port.`in`.web

import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionResDto
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.PaginatedRes

interface FindJoinablePromotionUseCase {
    suspend fun findJoinablePromotion(userId: Long, page: Long, size: Long): PaginatedRes<FindPromotionResDto>
}
package com.jinjindole2.jinpro.promotion.application.port.`in`.web

import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionJoinHistoryResDto
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.PaginatedRes

interface FindPromotionJoinHistoryUseCase {
    suspend fun findPromotionJoinHistory(userId: Long, page: Long, size: Long): PaginatedRes<FindPromotionJoinHistoryResDto>
}
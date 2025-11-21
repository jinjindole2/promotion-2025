package com.jinjindole2.dailypromotion.promotion.application.port.`in`.web

import com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto.FindPromotionJoinHistoryResDto
import com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto.PaginatedRes

interface FindPromotionJoinHistoryUseCase {
    suspend fun findPromotionJoinHistory(userId: Long, page: Long, size: Long): PaginatedRes<FindPromotionJoinHistoryResDto>
}
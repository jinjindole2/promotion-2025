package com.jinjindole2.dailypromotion.promotion.application.port.`in`.message

import com.jinjindole2.dailypromotion.promotion.domain.event.JoinPromotionEvent

interface SyncPromotionJoinHistoryUseCase {
    suspend fun syncToMySQL(event: JoinPromotionEvent)
    suspend fun syncToMongo(event: JoinPromotionEvent)
}
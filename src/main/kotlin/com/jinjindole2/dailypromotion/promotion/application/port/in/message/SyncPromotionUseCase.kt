package com.jinjindole2.dailypromotion.promotion.application.port.`in`.message

import com.jinjindole2.dailypromotion.promotion.domain.event.RegisterPromotionEvent

interface SyncPromotionUseCase {
    suspend fun syncToRedis(event: RegisterPromotionEvent)
    suspend fun syncToMongo(event: RegisterPromotionEvent)
}
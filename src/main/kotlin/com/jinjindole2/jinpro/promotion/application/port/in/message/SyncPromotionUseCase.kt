package com.jinjindole2.jinpro.promotion.application.port.`in`.message

import com.jinjindole2.jinpro.promotion.domain.event.RegisterPromotionEvent

interface SyncPromotionUseCase {
    suspend fun syncToRedis(event: RegisterPromotionEvent)
    suspend fun syncToMongo(event: RegisterPromotionEvent)
}
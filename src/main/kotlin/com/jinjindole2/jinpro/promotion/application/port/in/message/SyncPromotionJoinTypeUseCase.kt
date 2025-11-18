package com.jinjindole2.jinpro.promotion.application.port.`in`.message

import com.jinjindole2.jinpro.promotion.domain.event.AddPromotionJoinTypeEvent

interface SyncPromotionJoinTypeUseCase {
    suspend fun syncToRedis(event: AddPromotionJoinTypeEvent)
    suspend fun syncToMongo(event: AddPromotionJoinTypeEvent)
}
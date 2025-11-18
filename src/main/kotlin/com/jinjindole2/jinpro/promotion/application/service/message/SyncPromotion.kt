package com.jinjindole2.jinpro.promotion.application.service.message

import com.jinjindole2.jinpro.promotion.application.port.`in`.message.SyncPromotionUseCase
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionCachePort
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionMongoPort
import com.jinjindole2.jinpro.promotion.domain.event.RegisterPromotionEvent
import com.jinjindole2.jinpro.common.config.lock.Locker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SyncPromotion (
    private val cachePort: PromotionCachePort,
    private val mongoPort: PromotionMongoPort,
    private val locker: Locker,
) : SyncPromotionUseCase {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun syncToMongo(event: RegisterPromotionEvent) {
        mongoPort.registerPromotion(event.toDomain())
    }

    override suspend fun syncToRedis(event: RegisterPromotionEvent) {
        cachePort.setPromotionStock(event.toDomain())
    }
}
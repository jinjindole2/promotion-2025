package com.jinjindole2.jinpro.promotion.application.service.message

import com.jinjindole2.jinpro.promotion.application.port.`in`.message.SyncPromotionJoinTypeUseCase
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionCachePort
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionMongoPort
import com.jinjindole2.jinpro.promotion.domain.event.AddPromotionJoinTypeEvent
import com.jinjindole2.jinpro.common.config.lock.Locker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SyncPromotionJoinType (
    private val cachePort: PromotionCachePort,
    private val mongoPort: PromotionMongoPort,
    private val locker: Locker,
) : SyncPromotionJoinTypeUseCase {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun syncToMongo(event: AddPromotionJoinTypeEvent) {
        mongoPort.addJoinType(event.toDomain())
    }

    override suspend fun syncToRedis(event: AddPromotionJoinTypeEvent) {
        cachePort.addJoinType(event.toDomain())
    }
}
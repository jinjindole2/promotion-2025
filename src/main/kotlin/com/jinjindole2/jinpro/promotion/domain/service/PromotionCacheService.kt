package com.jinjindole2.jinpro.promotion.domain.service

import com.jinjindole2.jinpro.promotion.application.port.out.PromotionCachePort
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionPersistencePort
import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import com.jinjindole2.jinpro.common.exception.PromotionNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PromotionCacheService(
    private val cachePort: PromotionCachePort,
    private val persistencePort: PromotionPersistencePort
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private const val CACHE_TTL_MINUTES = 30L
    }

    suspend fun findPromotionWithCache(promoId: Long): Promotion {
        var adv = cachePort.findPromotion(promoId)

        if (adv == null) {
            logger.info("[프로모션 조회] 캐시 미스 - DB에서 조회 (promoId: $promoId)")
            adv = persistencePort.findPromotion(promoId)
                ?: throw PromotionNotFoundException("미등록 프로모션ID 입니다.")

            logger.info("[프로모션 조회] DB 조회 결과 캐시에 저장 (promoId: $promoId, TTL: ${CACHE_TTL_MINUTES}분)")
            cachePort.setPromotionWithTTL(adv, CACHE_TTL_MINUTES)
        } else {
            logger.info("[프로모션 조회] 캐시 히트 (promoId: $promoId)")
        }

        return adv
    }
}
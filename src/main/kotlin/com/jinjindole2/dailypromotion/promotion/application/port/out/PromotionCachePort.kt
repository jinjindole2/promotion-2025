package com.jinjindole2.dailypromotion.promotion.application.port.out

import com.jinjindole2.dailypromotion.promotion.domain.model.Promotion
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinType

interface PromotionCachePort {
    suspend fun findPromotion(promoId: Long): Promotion?
    suspend fun findPromotionStock(promoId: Long): Long?
    suspend fun joinPromotion(promotionUsers: PromotionJoinHistory): Long?
    suspend fun setPromotionStock(promotion: Promotion)
    suspend fun addJoinType(promotionJoinType: PromotionJoinType)
    suspend fun setPromotionWithTTL(promotion: Promotion, ttlMinutes: Long)
}
package com.jinjindole2.dailypromotion.promotion.application.port.`in`.web

import com.jinjindole2.dailypromotion.promotion.domain.model.Promotion
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinType

interface RegisterPromotionUseCase {
    suspend fun registerPromotion(adv: Promotion) : Promotion
    suspend fun addJoinType(promoJoinType: PromotionJoinType)
}
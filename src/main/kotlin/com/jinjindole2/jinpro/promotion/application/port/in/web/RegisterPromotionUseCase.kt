package com.jinjindole2.jinpro.promotion.application.port.`in`.web

import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinType

interface RegisterPromotionUseCase {
    suspend fun registerPromotion(adv: Promotion) : Promotion
    suspend fun addJoinType(promoJoinType: PromotionJoinType)
}
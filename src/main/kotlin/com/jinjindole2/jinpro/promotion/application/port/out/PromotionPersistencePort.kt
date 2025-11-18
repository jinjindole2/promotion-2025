package com.jinjindole2.jinpro.promotion.application.port.out

import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinType
import com.jinjindole2.jinpro.promotion.domain.model.LeadingPromotion

interface PromotionPersistencePort {
    suspend fun registerPromotion(adv: Promotion): Promotion?
    suspend fun joinPromotion(joinHistory: PromotionJoinHistory)
    suspend fun findPromotion(promoId: Long): Promotion?
    suspend fun addJoinType(promoJoinType: PromotionJoinType): PromotionJoinType?
    suspend fun addLeadingPromotion(leadAdv: LeadingPromotion): LeadingPromotion?
    suspend fun addLeadingPromotionAll(map: List<LeadingPromotion>)
    suspend fun decreasePromotionStock(promoId: Long): Int
}
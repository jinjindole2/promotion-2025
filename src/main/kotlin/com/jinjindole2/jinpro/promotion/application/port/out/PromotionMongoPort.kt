package com.jinjindole2.jinpro.promotion.application.port.out

import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionResDto
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionJoinHistoryResDto
import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinType

interface PromotionMongoPort {
    suspend fun countJoinable(userId: Long): Long?
    suspend fun findJoinableList(userId: Long, size: Long, l: Long): List<FindPromotionResDto>
    suspend fun countJoined(userId: Long): Long?
    suspend fun findJoinedList(userId: Long, size: Long, l: Long): List<FindPromotionJoinHistoryResDto>
    suspend fun registerPromotion(toDomain: Promotion)
    suspend fun joinPromotion(joinHistory: PromotionJoinHistory)
    suspend fun addJoinType(promotionJoinType: PromotionJoinType)
    suspend fun decreasePromotionStock(promoId: Long): Int
}
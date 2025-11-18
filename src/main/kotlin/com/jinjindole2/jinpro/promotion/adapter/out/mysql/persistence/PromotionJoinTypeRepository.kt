package com.jinjindole2.jinpro.promotion.adapter.out.mysql.persistence

import com.jinjindole2.jinpro.promotion.adapter.out.mysql.persistence.entity.PromotionJoinTypeEntity
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinType
import com.jinjindole2.jinpro.promotion.domain.model.JoinType
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface PromotionJoinTypeRepository : ReactiveCrudRepository<PromotionJoinTypeEntity, Long> {

    fun findByPromoIdAndJoinType(promoId: Long, joinType: JoinType) : Mono<PromotionJoinType>
    fun findByPromoId(promoId: Long) : Flow<PromotionJoinType>
}
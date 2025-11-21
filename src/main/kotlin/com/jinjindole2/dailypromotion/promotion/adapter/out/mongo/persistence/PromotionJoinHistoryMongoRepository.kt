package com.jinjindole2.dailypromotion.promotion.adapter.out.mongo.persistence

import com.jinjindole2.dailypromotion.promotion.adapter.out.mongo.persistence.document.PromotionJoinHistoryDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface PromotionJoinHistoryMongoRepository : ReactiveMongoRepository<PromotionJoinHistoryDocument, String> {
    fun save(joinHistory: PromotionJoinHistoryDocument): Mono<PromotionJoinHistoryDocument>
}
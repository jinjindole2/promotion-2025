package com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence

import com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.document.PromotionDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface PromotionMongoRepository : ReactiveMongoRepository<PromotionDocument, String> {
    //fun findById(id: String): Mono<PromotionDocument>
    //fun save(toDocument: PromotionDocument): Mono<PromotionDocument>
}
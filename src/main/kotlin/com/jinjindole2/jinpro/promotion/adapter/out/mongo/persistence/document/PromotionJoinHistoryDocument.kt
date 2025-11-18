package com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document(collection = "promotion_join_history")
@CompoundIndexes(
    value = [
        CompoundIndex(
            //name = "idx_join_date_user_promo",
            def = "{'joinDate': 1, 'userId': 1, 'promoId': 1}"
        ),
        CompoundIndex(
            //name = "idx_user_promo",
            def = "{'userId': 1, 'promoId': 1}"
        )
    ]
) // TODO 인덱스 이름을 이렇게해도 되나???
data class PromotionJoinHistoryDocument(
    @Id
    val id : ObjectId? = null,
    val promoId : String,
    val userId : String,
    val joinDate : LocalDate,
    val rewardAmount : Long,
    var createdDate : LocalDateTime? = null,
    var lastModifiedDate : LocalDateTime? = null,
)
package com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document(collection = "promotion")
@CompoundIndexes(
    value = [
        CompoundIndex(
            //name = "idx_date_joincount",
            def = "{'startDate': 1, 'endDate': 1, 'leftJoinCount': 1}"
        ),
        CompoundIndex(
            //name = "idx_reward_desc",
            def = "{'rewardAmount': -1}"
        ),
        CompoundIndex(
            //name = "idx_jointype",
            def = "{'joinTypes.joinType': 1}"
        )
    ]
) // TODO 인덱스명, 배열 내부 필드 멀티키 인덱스??
data class PromotionDocument (
    @Id
    val objectId: ObjectId? = null,
    val id : String? = null,
    var title : String,
    var rewardAmount : Long,
    var leftJoinCount : Long,
    var limitJoinCount : Long,
    var content : String?,
    var imageUrl : String?,
    var startDate : LocalDate,
    var endDate : LocalDate,
    val joinTypes: List<PromotionJoinTypeDocument>?,//= emptyList(),
    var createdDate : LocalDateTime? = null,
    var lastModifiedDate : LocalDateTime? = null,
)
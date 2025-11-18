package com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence

import com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.document.PromotionDocument
import com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.document.PromotionJoinTypeDocument
import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.LocalDate

@Repository
class PromotionCustomMongoRepository (
    private val mongoTemplate: ReactiveMongoTemplate
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    suspend fun addJoinType(
        promoId: String,
        newJoinType : PromotionJoinTypeDocument
    ) : UpdateResult? {
        // 1) 같은 joinType 원소가 있으면 전체 교체
        val qUpdate = Query(
            Criteria.where("id").`is`(promoId)
                .and("joinTypes.joinType").`is`(newJoinType.joinType)
        )
        val uUpdate = Update()
            .set("joinTypes.$", newJoinType)
            .set("lastModifiedDate", Instant.now())

        val updated = mongoTemplate.updateFirst(qUpdate, uUpdate, PromotionDocument::class.java)
            .awaitSingleOrNull()
        if (updated != null && updated.modifiedCount > 0) return updated//true

        // 2) 없으면 addToSet으로 객체 중복 최소화하며 추가
        val qAdd = Query(Criteria.where("id").`is`(promoId))
        val uAdd = Update()
            .addToSet("joinTypes", newJoinType) // 전체 객체 동일성 기준
            .set("lastModifiedDate", Instant.now())

        return mongoTemplate.updateFirst(qAdd, uAdd, PromotionDocument::class.java).awaitSingleOrNull()
    }

    suspend fun findJoinablePromotionsPaged(
        userId: String,
        today: LocalDate,
        page: Int,
        size: Int
    ): List<PromotionDocument> = withContext(Dispatchers.IO) {

        val aggregationPipeline = listOf(

            // 참여 가능한 일자 및 횟수의 프로모션 조회
            Document("\$match", Document().apply {
                put("startDate", Document("\$lte", today))
                put("endDate", Document("\$gte", today))
                put("limitJoinCount", Document("\$gt", 0))
            }),

            // 당일 참여한 프로모션 제외
            Document("\$lookup", Document().apply {
                put("from", "promotion_join_history")
                put("let", Document("promoId", "\$id"))
                put("pipeline", listOf(
                    Document("\$match", Document("\$expr", Document("\$and", listOf(
                        Document("\$eq", listOf("\$promoId", "\$\$promoId")),
                        Document("\$eq", listOf("\$userId", userId)),
                        Document("\$eq", listOf("\$joinDate", today))
                    ))))
                ))
                put("as", "todayJoins")
            }),
            Document("\$match", Document("todayJoins", Document("\$size", 0))),

            // N_DUP_LIMIT 값 추출
            Document("\$addFields", Document("ndupLimitValue",
                Document("\$arrayElemAt", listOf(
                    Document("\$map", Document().apply {
                        put("input", Document("\$filter", Document().apply {
                            put("input", "\$joinTypes")
                            put("as", "jt")
                            put("cond", Document("\$eq", listOf("\$\$jt.joinType", "N_DUP_LIMIT")))
                        }))
                        put("as", "item")
                        put("in", "\$\$item.limitDupJoinCount")
                    }),
                    0
                ))
            )),

            // 고객 중복 참여이력 카운트
            Document("\$lookup", Document().apply {
                put("from", "promotion_join_history")
                put("let", Document("promoId", "\$id"))
                put("pipeline", listOf(
                    Document("\$match", Document("\$expr", Document("\$and", listOf(
                        Document("\$eq", listOf("\$promoId", "\$\$promoId")),
                        Document("\$eq", listOf("\$userId", userId)),
                    ))))
                ))
                put("as", "userJoinHistory")
            }),
            Document("\$addFields", Document("dupJoinCount", Document("\$size", "\$userJoinHistory"))),
            Document("\$match", Document("\$or", listOf(
                Document("ndupLimitValue", Document("\$exists", false)),
                Document("ndupLimitValue", null),
                Document("\$expr", Document("\$lt", listOf("\$dupJoinCount", "\$ndupLimitValue")))
            ))),

            // LEADING 조건 추출
            Document("\$addFields", Document("leadingPromoIds",
                Document("\$arrayElemAt", listOf(
                    Document("\$map", Document().apply {
                        put("input", Document("\$filter", Document().apply {
                            put("input", "\$joinTypes")
                            put("as", "jt")
                            put("cond", Document("\$eq", listOf("\$\$jt.joinType", "LEADING")))
                        }))
                        put("as", "item")
                        put("in", "\$\$item.leadPromoIdList")
                    }),
                    0
                ))
            )),

            // 선행프로모션 참여여부 확인
            Document("\$lookup", Document().apply {
                put("from", "promotion_join_history")
                put("let", Document("leadIds", "\$leadingPromoIds"))
                put("pipeline", listOf(
                    Document("\$match", Document("\$expr", Document("\$and", listOf(
                        Document("\$eq", listOf("\$promoId", "\$\$leadIds")),
                        Document("\$eq", listOf("\$userId", userId)),
                    ))))
                ))
                put("as", "leadJoins")
            }),
            Document("\$match", Document("\$or", listOf(
                Document("leadingPromoIds", Document("\$exists", false)),
                Document("leadingPromoIds", null),
                Document("leadJoins", Document("\$not", Document("\$size", 0)))
            ))),

            // 정렬 및 페이징
            Document("\$sort", Document("rewardAmount", -1)),
            Document("\$skip", (page * size).toLong()),
            Document("\$limit", size.toLong())
        )

        val results = mongoTemplate.execute("promotion") { collection ->
            collection.aggregate(aggregationPipeline)
        }.asFlow().toList()

        results.map { doc ->
            mongoTemplate.converter.read(PromotionDocument::class.java, doc)
        }
    }

    suspend fun countJoinablePromotions(
        userId: String,
        today: LocalDate
    ): Long = withContext(Dispatchers.IO) {

        val aggregationPipeline = listOf(

            // 참여 가능한 일자 및 횟수의 프로모션 조회
            Document("\$match", Document().apply {
                put("startDate", Document("\$lte", today))
                put("endDate", Document("\$gte", today))
                put("limitJoinCount", Document("\$gt", 0))
            }),

            // 당일 참여한 프로모션 제외
            Document("\$lookup", Document().apply {
                put("from", "promotion_join_history")
                put("let", Document("promoId", "\$id"))
                put("pipeline", listOf(
                    Document("\$match", Document("\$expr", Document("\$and", listOf(
                        Document("\$eq", listOf("\$promoId", "\$\$promoId")),
                        Document("\$eq", listOf("\$userId", userId)),
                        Document("\$eq", listOf("\$joinDate", today))
                    ))))
                ))
                put("as", "todayJoins")
            }),
            Document("\$match", Document("todayJoins", Document("\$size", 0))),

            // N_DUP_LIMIT 값 추출
            Document("\$addFields", Document("ndupLimitValue",
                Document("\$arrayElemAt", listOf(
                    Document("\$map", Document().apply {
                        put("input", Document("\$filter", Document().apply {
                            put("input", "\$joinTypes")
                            put("as", "jt")
                            put("cond", Document("\$eq", listOf("\$\$jt.joinType", "N_DUP_LIMIT")))
                        }))
                        put("as", "item")
                        put("in", "\$\$item.limitDupJoinCount")
                    }),
                    0
                ))
            )),

            // 고객 중복 참여이력 카운트
            Document("\$lookup", Document().apply {
                put("from", "promotion_join_history")
                put("let", Document("promoId", "\$id"))
                put("pipeline", listOf(
                    Document("\$match", Document("\$expr", Document("\$and", listOf(
                        Document("\$eq", listOf("\$promoId", "\$\$promoId")),
                        Document("\$eq", listOf("\$userId", userId)),
                    ))))
                ))
                put("as", "userJoinHistory")
            }),
            Document("\$addFields", Document("dupJoinCount", Document("\$size", "\$userJoinHistory"))),
            Document("\$match", Document("\$or", listOf(
                Document("ndupLimitValue", Document("\$exists", false)),
                Document("ndupLimitValue", null),
                Document("\$expr", Document("\$lt", listOf("\$dupJoinCount", "\$ndupLimitValue")))
            ))),

            // LEADING 조건 추출
            Document("\$addFields", Document("leadingPromoIds",
                Document("\$arrayElemAt", listOf(
                    Document("\$map", Document().apply {
                        put("input", Document("\$filter", Document().apply {
                            put("input", "\$joinTypes")
                            put("as", "jt")
                            put("cond", Document("\$eq", listOf("\$\$jt.joinType", "LEADING")))
                        }))
                        put("as", "item")
                        put("in", "\$\$item.leadPromoIdList")
                    }),
                    0
                ))
            )),

            // 선행프로모션 참여여부 확인
            Document("\$lookup", Document().apply {
                put("from", "promotion_join_history")
                put("let", Document("leadIds", "\$leadingPromoIds"))
                put("pipeline", listOf(
                    Document("\$match", Document("\$expr", Document("\$and", listOf(
                        Document("\$eq", listOf("\$promoId", "\$\$leadIds")),
                        Document("\$eq", listOf("\$userId", userId)),
                    ))))
                ))
                put("as", "leadJoins")
            }),
            Document("\$match", Document("\$or", listOf(
                Document("leadingPromoIds", Document("\$exists", false)),
                Document("leadingPromoIds", null),
                Document("leadJoins", Document("\$not", Document("\$size", 0)))
            ))),

            // 카운팅
            Document("\$count", "totalCount")
        )

        val result = mongoTemplate.execute("promotion") { collection ->
            collection.aggregate(aggregationPipeline).first()
        }.awaitSingle()

        result?.getInteger("totalCount")?.toLong() ?: 0L
    }

    suspend fun countJoinedPromotions(userId: String): Long = withContext(Dispatchers.IO) {
        val countPipeline = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("userId").`is`(userId)),
            Aggregation.count().`as`("totalCount")
        )

        val result = mongoTemplate.aggregate(countPipeline, "promotion_join_history", Document::class.java)
            .asFlow().toList()
        result.firstOrNull()?.getInteger("totalCount")?.toLong() ?: 0L
    }

    suspend fun findJoinedPromotionsPaged(
        userId: String,
        page: Int,
        size: Int
    ): List<Document> = withContext(Dispatchers.IO) {

        val pipeline = Aggregation.newAggregation(
            // 1. 해당 유저의 참여 이력만 조회
            Aggregation.match(Criteria.where("userId").`is`(userId)),

            // 2. 프로모션 정보와 조인
            Aggregation.lookup("promotion", "promoId", "id", "promotion"),

            // 3. 프로모션 정보 펼치기 (배열을 단일 객체로)
            Aggregation.unwind("promotion"),

            // 4. 필요한 필드만 프로젝션
            Aggregation.project()
                .and("promoId").`as`("promoId")
                .and("userId").`as`("userId")
                .and("joinDate").`as`("joinDate")
                .and("promotion.title").`as`("title")
                .and("promotion.description").`as`("description")
                .and("promotion.startDate").`as`("startDate")
                .and("promotion.endDate").`as`("endDate")
                .and("promotion.limitJoinCount").`as`("limitJoinCount"),

            // 5. 최신 참여일 순으로 정렬
            Aggregation.sort(Sort.Direction.DESC, "joinDate"),

            // 6. 페이징
            Aggregation.skip((page * size).toLong()),
            Aggregation.limit(size.toLong())
        )

        mongoTemplate.aggregate(pipeline, "promotion_join_history", Document::class.java)
            .asFlow().toList()
    }

    suspend fun decreasePromotionStock(promoId: String): Int {
        val query = Query(
            Criteria.where("id").`is`(promoId)
                .and("limitJoinCount").gt(0)
        )
        val update = Update()
            .inc("limitJoinCount", -1)
            .set("lastModifiedDate", Instant.now())

        val result = mongoTemplate.updateFirst(query, update, PromotionDocument::class.java)
            .awaitSingleOrNull()

        return result?.modifiedCount?.toInt() ?: 0
    }
}
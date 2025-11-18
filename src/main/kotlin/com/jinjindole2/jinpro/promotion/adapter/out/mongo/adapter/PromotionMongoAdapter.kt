package com.jinjindole2.jinpro.promotion.adapter.out.mongo.adapter

import com.jinjindole2.jinpro.promotion.application.port.out.PromotionMongoPort
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionResDto
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionJoinHistoryResDto
import com.jinjindole2.jinpro.promotion.adapter.out.mongo.mapper.PromotionMongoMapper
import com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.PromotionCustomMongoRepository
import com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.PromotionJoinHistoryMongoRepository
import com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.PromotionMongoRepository
import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinType
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Repository
import java.time.LocalDate
import org.bson.Document

@Repository
class PromotionMongoAdapter (
    private val customRepository: PromotionCustomMongoRepository,
    private val promoRepository: PromotionMongoRepository,
    private val joinHistRepository: PromotionJoinHistoryMongoRepository,
    private val mongoMapper: PromotionMongoMapper
) : PromotionMongoPort {

    override suspend fun countJoinable(userId: Long): Long? {
        return customRepository.countJoinablePromotions(
            userId = userId.toString(),
            today = LocalDate.now()
        )
    }

    override suspend fun findJoinableList(userId: Long, size: Long, offset: Long): List<FindPromotionResDto> {
        val page = (offset / size).toInt()
        val documents = customRepository.findJoinablePromotionsPaged(
            userId = userId.toString(),
            today = LocalDate.now(),
            page = page,
            size = size.toInt()
        )
        return documents.map { mongoMapper.documentToResDto(it) }
    }

    override suspend fun countJoined(userId: Long): Long? {
        return customRepository.countJoinedPromotions(userId.toString())
    }

    override suspend fun findJoinedList(userId: Long, size: Long, offset: Long): List<FindPromotionJoinHistoryResDto> {
        val page = (offset / size).toInt()
        val documents = customRepository.findJoinedPromotionsPaged(
            userId = userId.toString(),
            page = page,
            size = size.toInt()
        )
        return documents.map { documentToJoinHistoryResDto(it) }
    }

    override suspend fun registerPromotion(promotion: Promotion) {
        // TODO 예외처리 -> 재처리?
        promoRepository.save(mongoMapper.domainToDocument(promotion)).awaitSingle() ?: Exception("")
    }

    override suspend fun joinPromotion(joinHistory: PromotionJoinHistory) {
        // TODO 예외처리 -> 재처리?
        joinHistRepository.save(mongoMapper.domainToDocument(joinHistory)).awaitSingle() ?: Exception("")
    }

    override suspend fun addJoinType(promotionJoinType: PromotionJoinType) {
        customRepository.addJoinType(promotionJoinType.promoId.toString(), mongoMapper.domainToDocument(promotionJoinType))
    }

    override suspend fun decreasePromotionStock(promoId: Long): Int {
        return customRepository.decreasePromotionStock(promoId.toString())
    }

    private fun documentToJoinHistoryResDto(document: Document): FindPromotionJoinHistoryResDto {
        return FindPromotionJoinHistoryResDto(
            promoId = document.getLong("promoId") ?: 0L,
            userId = document.getLong("userId") ?: 0L,
            joinDate = document.get("joinDate") as? LocalDate ?: LocalDate.now(),
            title = document.getString("title") ?: "",
            rewardAmount = 0L // MongoDB에서 rewardAmount 정보가 없으므로 기본값 설정
        )
    }
}
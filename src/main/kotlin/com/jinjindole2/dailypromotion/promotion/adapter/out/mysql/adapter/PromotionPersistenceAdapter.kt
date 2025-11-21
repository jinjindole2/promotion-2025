package com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.adapter

import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.PromotionJoinHistoryRepository
import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.PromotionJoinTypeRepository
import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.PromotionRepository
import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.LeadingPromotionRepository
import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.mapper.PromotionPersistenceMapper
import com.jinjindole2.dailypromotion.promotion.application.port.out.PromotionPersistencePort
import com.jinjindole2.dailypromotion.promotion.domain.model.Promotion
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinType
import com.jinjindole2.dailypromotion.promotion.domain.model.LeadingPromotion
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Repository

@Repository
class PromotionPersistenceAdapter (
    private val promoRepository: PromotionRepository,
    private val joinTypeRepository: PromotionJoinTypeRepository,
    private val leadingPromoRepository: LeadingPromotionRepository,
    private val joinHistRepository: PromotionJoinHistoryRepository,
    private val persistenceMapper: PromotionPersistenceMapper
) : PromotionPersistencePort {

    override suspend fun registerPromotion(promo: Promotion): Promotion? {
        return promoRepository.save(persistenceMapper.domainToEntity(promo)).awaitSingleOrNull()?.let { persistenceMapper.entityToDomain(it) }
    }

    override suspend fun joinPromotion(joinHistory: PromotionJoinHistory) {
        joinHistRepository.save(persistenceMapper.domainToEntity(joinHistory)).awaitSingle()
    }

    override suspend fun findPromotion(promoId: Long): Promotion? {
        return promoRepository.findById(promoId).awaitSingleOrNull()?.let { persistenceMapper.entityToDomain(it) }
    }

    override suspend fun addJoinType(promoJoinType: PromotionJoinType): PromotionJoinType? {
        return joinTypeRepository.save(persistenceMapper.domainToEntity(promoJoinType)).awaitSingleOrNull()?.let { persistenceMapper.entityToDomain(it) }
    }

    override suspend fun addLeadingPromotion(leadPromo: LeadingPromotion): LeadingPromotion? {
        return leadingPromoRepository.save(persistenceMapper.domainToEntity(leadPromo)).awaitSingleOrNull()?.let { persistenceMapper.entityToDomain(it) }
    }

    override suspend fun addLeadingPromotionAll(leadPromoList: List<LeadingPromotion>) {
        leadingPromoRepository.saveAll(leadPromoList.map{persistenceMapper.domainToEntity(it)}).awaitSingle()
    }

    override suspend fun decreasePromotionStock(promoId: Long): Int {
        return promoRepository.decreaseStockById(promoId)
    }
}
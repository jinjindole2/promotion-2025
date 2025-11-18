package com.jinjindole2.jinpro.promotion.adapter.out.mysql.persistence

import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionJoinHistoryResDto
import com.jinjindole2.jinpro.promotion.adapter.out.mysql.persistence.entity.PromotionJoinHistoryEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.time.LocalDate

interface PromotionJoinHistoryRepository : ReactiveCrudRepository<PromotionJoinHistoryEntity, Long> {

    @Query("""
            SELECT PU.join_date
                 , PU.user_id
                 , PU.promo_id
                 , PRO.title
                 , PRO.reward_amount
            FROM (
                SELECT PU.join_date
                     , PU.user_id
                     , PU.promo_id
                FROM promotion_users PU
                WHERE PU.user_id = :userId
                ORDER BY PU.join_date DESC
                LIMIT :limit OFFSET :offset
            ) PU
            LEFT JOIN promotion PRO
            ON PRO.id = PU.promo_id
            ORDER BY PU.join_date DESC
            """)
    fun findJoinedList(userId: Long, limit: Long, offset: Long): Flow<FindPromotionJoinHistoryResDto>

    @Query("""SELECT count(1)
              FROM promotion_users PU
              WHERE PU.user_id = :userId""")
    fun findJoinedListCount(userId: Long): Mono<Long>

    fun findByPromoIdAndUserIdAndJoinDate(promoId: Long, userId: Long, joinDate: LocalDate): Mono<PromotionJoinHistoryEntity>
}
package com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence

import com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto.FindPromotionResDto
import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface PromotionRepository : ReactiveCrudRepository<PromotionEntity, Long> {

    @Query("""SELECT PRO.*
              FROM promotion PRO
              WHERE PRO.start_date <= CURDATE()
                AND PRO.end_date >= CURDATE()
                AND PRO.left_join_count > 0
                AND NOT EXISTS (
                    SELECT 1
                    FROM promotion_users PU
                    WHERE PU.promo_id = PRO.id
                      AND PU.user_id = :userId
                      AND PU.join_date = CURDATE()
                  )
                AND NOT EXISTS (
                    SELECT 1
                    FROM promotion_join_type PJT
                    WHERE PJT.promo_id = PRO.id
                      AND PJT.join_type = 'N_DUP_LIMIT'
                      AND PJT.limit_dup_join_count <= (
                              SELECT COUNT(1)
                              FROM promotion_users PU
                              WHERE PU.promo_id = PJT.promo_id
                                AND PU.user_id = :userId
                          )
                  )
                AND NOT EXISTS (
                    SELECT 1
                    FROM promotion_join_type PJT
                    JOIN leading_promotion LP ON PJT.promo_id = LP.trail_promo_id
                    WHERE PJT.promo_id = PRO.id
                      AND PJT.join_type = 'LEADING'
                      AND NOT EXISTS (
                            SELECT 1
                            FROM promotion_users PU2
                            WHERE PU2.promo_id = LP.lead_promo_id
                              AND PU2.user_id = :userId
                        )
                  )
              ORDER BY reward_amount DESC
              LIMIT :limit OFFSET :offset""")
    fun findJoinableList(userId: Long, limit: Long, offset: Long): Flow<FindPromotionResDto>

    @Query("""SELECT count(1)
              FROM promotion PRO
              WHERE PRO.start_date <= CURDATE()
                AND PRO.end_date >= CURDATE()
                AND PRO.left_join_count > 0
                AND NOT EXISTS (
                    SELECT 1
                    FROM promotion_join_type PJT
                    WHERE PJT.promo_id = PRO.id
                      AND PJT.join_type = 'N_DUP_LIMIT'
                      AND PJT.limit_dup_join_count <= (
                          SELECT COUNT(1)
                          FROM promotion_users PU
                          WHERE PU.promo_id = PJT.promo_id
                            AND PU.user_id = :userId
                      )
                  )
                AND NOT EXISTS (
                    SELECT 1
                    FROM promotion_join_type PJT
                    JOIN leading_promotion LP ON PJT.promo_id = LP.trail_promo_id
                    WHERE PJT.promo_id = PRO.id
                      AND PJT.join_type = 'LEADING'
                      AND NOT EXISTS (
                          SELECT 1
                          FROM promotion_users PU2
                          WHERE PU2.promo_id = LP.lead_promo_id
                            AND PU2.user_id = :userId
                      )
                  )""")
    fun findJoinableListCount(userId: Long): Mono<Long>

    @Query("UPDATE promotion SET left_join_count = :stock WHERE id = :id")
    suspend fun updateStockById(id: Long, stock: Long): Int

    @Query("UPDATE promotion SET left_join_count = left_join_count - 1 WHERE id = :id AND left_join_count > 0")
    suspend fun decreaseStockById(id: Long): Int
}
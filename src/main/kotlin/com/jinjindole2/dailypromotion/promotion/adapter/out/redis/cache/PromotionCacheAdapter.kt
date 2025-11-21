package com.jinjindole2.dailypromotion.promotion.adapter.out.redis.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.jinjindole2.dailypromotion.promotion.application.port.out.PromotionCachePort
import com.jinjindole2.dailypromotion.promotion.domain.model.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.List

@Component
class PromotionCacheAdapter (
    @Qualifier("reactiveStringRedisTemplate")
    private val reactiveStringRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) : PromotionCachePort {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val ops: ReactiveValueOperations<String, String> = reactiveStringRedisTemplate.opsForValue()

    companion object {
        private const val CACHE_TTL_MINUTES = 30L
    }

    override suspend fun findPromotion(promoId: Long): Promotion? {
        return ops.get("promoId:${promoId}").awaitSingleOrNull()?.let { json ->
            try {
                objectMapper.readValue(json, Promotion::class.java)
            } catch (e: Exception) {
                logger.error("Failed to deserialize Promotion from Redis: ${e.message}", e)
                null
            }
        }
    }

    override suspend fun findPromotionStock(promoId: Long): Long? {
        return ops.get("promoCache:${promoId}:stock").awaitSingleOrNull()?.let { stock ->
            try {
                stock.toLong()
            } catch (e: Exception) {
                logger.error("Failed to parse stock from Redis: ${e.message}", e)
                null
            }
        }
    }

    override suspend fun joinPromotion(promoUser: PromotionJoinHistory): Long? {
        return reactiveStringRedisTemplate.execute(
            joinScript(),
            List.of<String>("promoCache:${promoUser.promoId}:stock",
                "promoUser:promoId:${promoUser.promoId}userId${promoUser.userId}"),
            List.of<String>(promoUser.joinDate.toString(), promoUser.promoId.toString(), promoUser.userId.toString())
        ).awaitSingle()
    }

    override suspend fun addJoinType(savedPromoJoinType: PromotionJoinType) {
        try {
            when (savedPromoJoinType.joinType) {
                JoinType.LEADING -> {
                    val leadPromoIds = savedPromoJoinType.leadPromoIdList ?: emptyList()
                    reactiveStringRedisTemplate.execute(
                        replaceLeadingScript(),
                        List.of("promoCache:${savedPromoJoinType.promoId}:joinType:LEADING"),
                        leadPromoIds.map { it.toString() }
                    ).awaitSingle()
                    logger.info("Updated LEADING joinType (replaced atomically): promoId=${savedPromoJoinType.promoId}, leadPromoIds=${savedPromoJoinType.leadPromoIdList}")
                }
                JoinType.N_DUP_LIMIT -> {
                    // String으로 저장 (덮어쓰기)
                    ops.set("promoCache:${savedPromoJoinType.promoId}:joinType:N_DUP_LIMIT",
                        savedPromoJoinType.limitDupJoinCount.toString())
                        .awaitSingle()
                    logger.info("Updated N_DUP_LIMIT joinType: promoId=${savedPromoJoinType.promoId}, limit=${savedPromoJoinType.limitDupJoinCount}")
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to add joinType: ${e.message}", e)
            throw e
        }
    }

    private fun replaceLeadingScript(): RedisScript<Long> {
        /**
         * KEYS[1]: LEADING Set 키 (promoCache:{promoId}:joinType:LEADING)
         * ARGV[...]: leadPromoId 리스트
         */
        val script: String = """
                -- 원자적으로 삭제 후 재생성
                redis.call('DEL', KEYS[1])
                for i = 1, #ARGV do
                    redis.call('SADD', KEYS[1], ARGV[i])
                end
                return 0
                """
        return RedisScript.of<Long>(script, Long::class.java)
    }

    private fun joinScript(): RedisScript<Long> {
        /**
         * KEYS[1]: 프로모션 재고 키 (promoCache:{promoId}:stock)
         * KEYS[2]: 프로모션 참여이력 Set 키 (promoUser:promoId:{promoId}userId{userId})
         * ARGV[1]: 오늘 날짜 (yyyyMMdd)
         * ARGV[2]: promoId
         * ARGV[3]: userId
         */
        val script: String = """
                -- 1. 재고 확인
                local stock = redis.call('GET', KEYS[1])
                if not stock then
                    return -1
                end

                if tonumber(stock) <= 0 then
                    return -2
                end

                -- 2. 중복 참여 제한 확인 (N_DUP_LIMIT)
                local nDupLimit = redis.call('GET', 'promoCache:' .. ARGV[2] .. ':joinType:N_DUP_LIMIT')
                if nDupLimit then
                    local userJoinCount = redis.call('SCARD', KEYS[2])
                    if userJoinCount >= tonumber(nDupLimit) then
                        return -3
                    end
                end

                -- 3. 선행프로모션 확인 (LEADING)
                local leadingPromoIds = redis.call('SMEMBERS', 'promoCache:' .. ARGV[2] .. ':joinType:LEADING')
                if #leadingPromoIds > 0 then
                    local hasLeadingParticipation = false
                    for i = 1, #leadingPromoIds do
                        local leadPromoId = leadingPromoIds[i]
                        local leadKey = 'promoUser:promoId:' .. leadPromoId .. 'userId' .. ARGV[3]
                        if redis.call('SCARD', leadKey) > 0 then
                            hasLeadingParticipation = true
                            break
                        end
                    end
                    if not hasLeadingParticipation then
                        return -4
                    end
                end

                -- 4. 이미 참여했는지 확인
                if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
                    return -5
                end

                -- 5. 참여 처리 (재고 감소 + 참여 기록)
                redis.call('DECR', KEYS[1])
                redis.call('SADD', KEYS[2], ARGV[1])

                return 0
                """
        return RedisScript.of<Long>(script, Long::class.java)
    }

    override suspend fun setPromotionStock(promo: Promotion) {
        try {
            // 재고만 저장 (String)
            ops.set("promoCache:${promo.id}:stock", promo.limitJoinCount.toString()).awaitSingle()

            logger.info("Successfully cached promotion stock: promoId=${promo.id}, stock=${promo.limitJoinCount}")
        } catch (e: Exception) {
            logger.error("Failed to cache promotion stock: promoId=${promo.id}, error=${e.message}", e)
            throw e
        }
    }

    override suspend fun setPromotionWithTTL(promotion: Promotion, ttlMinutes: Long) {
        try {
            val promoJson = objectMapper.writeValueAsString(promotion)
            ops.set("promoId:${promotion.id}", promoJson, Duration.ofMinutes(ttlMinutes)).awaitSingle()
            logger.info("Successfully cached promotion with TTL: promoId=${promotion.id}, ttl=${ttlMinutes}m")
        } catch (e: Exception) {
            logger.error("Failed to cache promotion with TTL: promoId=${promotion.id}, error=${e.message}", e)
            throw e
        }
    }
}
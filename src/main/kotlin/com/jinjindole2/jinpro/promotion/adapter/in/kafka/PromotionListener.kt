package com.jinjindole2.jinpro.promotion.adapter.`in`.kafka

import com.jinjindole2.jinpro.promotion.application.port.`in`.message.SyncPromotionJoinHistoryUseCase
import com.jinjindole2.jinpro.promotion.application.port.`in`.message.SyncPromotionJoinTypeUseCase
import com.jinjindole2.jinpro.promotion.application.port.`in`.message.SyncPromotionUseCase
import com.jinjindole2.jinpro.promotion.domain.event.AddPromotionJoinTypeEvent
import com.jinjindole2.jinpro.promotion.domain.event.JoinPromotionEvent
import com.jinjindole2.jinpro.promotion.domain.event.RegisterPromotionEvent
import com.jinjindole2.jinpro.common.logging.TraceContextManager
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PromotionListener (
    private val syncAdvUseCase: SyncPromotionUseCase,
    private val syncAddJoinTypeUseCase: SyncPromotionJoinTypeUseCase,
    private val syncJoinHistUseCase: SyncPromotionJoinHistoryUseCase
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    // 프로모션등록 Redis 동기화
    @KafkaListener(topics = ["promotion-register"], groupId = "redis-sync-group")
    fun syncRegisterToRedis(event: RegisterPromotionEvent) = runBlocking {
        TraceContextManager.withTraceContext(event) {
            logger.info("[프로모션등록 Redis 동기화 시작] ${event.toLogString()}")
            try {
                syncAdvUseCase.syncToRedis(event)
                logger.info("[프로모션등록 Redis 동기화 완료] promoId=${event.id}")
            } catch (e: Exception) {
                logger.error("[프로모션등록 Redis 동기화 실패] promoId=${event.id}", e)
                throw e
            }
        }
    }

    // 프로모션등록 MongoDB 동기화
    @KafkaListener(topics = ["promotion-register"], groupId = "mongo-sync-group")
    fun syncRegisterToMongo(event: RegisterPromotionEvent) = runBlocking {
        TraceContextManager.withTraceContext(event) {
            logger.info("[프로모션등록 MongoDB 동기화 시작] ${event.toLogString()}")
            try {
                syncAdvUseCase.syncToMongo(event)
                logger.info("[프로모션등록 MongoDB 동기화 완료] promoId=${event.id}")
            } catch (e: Exception) {
                logger.error("[프로모션등록 MongoDB 동기화 실패] promoId=${event.id}", e)
                throw e
            }
        }
    }

    // 프로모션 참여타입 Redis 동기화
    @KafkaListener(topics = ["promotion-add-join-type"], groupId = "redis-sync-group")
    fun syncAddJoinTypeToRedis(event: AddPromotionJoinTypeEvent) = runBlocking {
        TraceContextManager.withTraceContext(event) {
            logger.info("[참여타입 Redis 동기화 시작] ${event.toLogString()}")
            try {
                syncAddJoinTypeUseCase.syncToRedis(event)
                logger.info("[참여타입 Redis 동기화 완료] promoId=${event.promoId}, joinType=${event.joinType}")
            } catch (e: Exception) {
                logger.error("[참여타입 Redis 동기화 실패] promoId=${event.promoId}, joinType=${event.joinType}", e)
                throw e
            }
        }
    }

    // 프로모션 참여타입 MongoDB 동기화
    @KafkaListener(topics = ["promotion-add-join-type"], groupId = "mongo-sync-group")
    fun syncAddJoinTypeToMongo(event: AddPromotionJoinTypeEvent) = runBlocking {
        TraceContextManager.withTraceContext(event) {
            logger.info("[참여타입 MongoDB 동기화 시작] ${event.toLogString()}")
            try {
                syncAddJoinTypeUseCase.syncToMongo(event)
                logger.info("[참여타입 MongoDB 동기화 완료] promoId=${event.promoId}, joinType=${event.joinType}")
            } catch (e: Exception) {
                logger.error("[참여타입 MongoDB 동기화 실패] promoId=${event.promoId}, joinType=${event.joinType}", e)
                throw e
            }
        }
    }

    // 프로모션 참여 MySQL 동기화 TODO 배치성으로 처리 -> 요청 과부하는 replica set 에서 read write 구분으로 해결하자. 건당 처리. 하지만 재고가 꼬일수 있으니까 락을 좀 걸자.
    @KafkaListener(topics = ["promotion-join"], groupId = "mysql-sync-group")
    fun syncJoinToMySQL(event: JoinPromotionEvent) = runBlocking {
        TraceContextManager.withTraceContext(event) {
            logger.info("[프로모션 참여 MySQL 동기화 시작] ${event.toLogString()}")
            try {
                syncJoinHistUseCase.syncToMySQL(event)
                logger.info("[프로모션 참여 MySQL 동기화 완료] promoId=${event.promoId}, userId=${event.userId}")
            } catch (e: Exception) {
                logger.error("[프로모션 참여 MySQL 동기화 실패] promoId=${event.promoId}, userId=${event.userId}", e)
                throw e
            }
        }
    }

    // 프로모션 참여 MongoDB 동기화
    @KafkaListener(topics = ["promotion-join"], groupId = "mongo-sync-group")
    fun syncJoinToMongo(event: JoinPromotionEvent) = runBlocking {
        TraceContextManager.withTraceContext(event) {
            logger.info("[프로모션 참여 MongoDB 동기화 시작] ${event.toLogString()}")
            try {
                syncJoinHistUseCase.syncToMongo(event)
                logger.info("[프로모션 참여 MongoDB 동기화 완료] promoId=${event.promoId}, userId=${event.userId}")
            } catch (e: Exception) {
                logger.error("[프로모션 참여 MongoDB 동기화 실패] promoId=${event.promoId}, userId=${event.userId}", e)
                throw e
            }
        }
    }

    // 프로모션 참여 리워드 API 호출
    @KafkaListener(topics = ["promotion-join"], groupId = "reward-api-group")
    fun reqRewardApi(event: JoinPromotionEvent) = runBlocking {
        TraceContextManager.withTraceContext(event) {
            logger.info("[리워드 API 호출 시작] ${event.toLogString()}")
            try {
                // TODO: 리워드 API 구현
                logger.info("[리워드 API 호출 완료] promoId=${event.promoId}, userId=${event.userId}, rewardAmount=${event.rewardAmount}")
            } catch (e: Exception) {
                logger.error("[리워드 API 호출 실패] promoId=${event.promoId}, userId=${event.userId}, rewardAmount=${event.rewardAmount}", e)
                throw e
            }
        }
    }
}
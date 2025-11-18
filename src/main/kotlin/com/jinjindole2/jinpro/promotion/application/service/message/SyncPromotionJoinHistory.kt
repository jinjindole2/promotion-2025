package com.jinjindole2.jinpro.promotion.application.service.message

import com.jinjindole2.jinpro.promotion.application.port.`in`.message.SyncPromotionJoinHistoryUseCase
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionMongoPort
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionPersistencePort
import com.jinjindole2.jinpro.promotion.domain.event.JoinPromotionEvent
import com.jinjindole2.jinpro.common.config.lock.Locker
import com.jinjindole2.jinpro.common.exception.PromotionNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SyncPromotionJoinHistory (
    private val mongoPort: PromotionMongoPort,
    private val persistencePort: PromotionPersistencePort,
    private val locker: Locker,
) : SyncPromotionJoinHistoryUseCase {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Transactional("transactionManager")  // MySQL/R2DBC 트랜잭션 매니저
    override suspend fun syncToMySQL(event: JoinPromotionEvent) {
        locker.lock("sync-mysql-${event.promoId}") {
            try {
                // 1. 참여 이력 저장 (유니크 인덱스가 중복을 방지함)
                persistencePort.joinPromotion(event.toDomain())

                // 2. 프로모션 조회
                val promotion = persistencePort.findPromotion(event.promoId) ?: throw PromotionNotFoundException("프로모션를 찾을 수 없습니다: ${event.promoId}")
                if (promotion.leftJoinCount <= 0) {
                    throw Exception("재고가 0인 프로모션 : promoId=${event.promoId}") // TODO 예외처리 어떻게 할지
                }

                // 3. 프로모션 재고 감소 (재고가 0보다 클 때만 감소)
                if (persistencePort.decreasePromotionStock(event.promoId) == 0) {
                    throw Exception("재고가 이미 0인 프로모션에 참여 시도: promoId=${event.promoId}") // TODO 예외처리 어떻게 할지
                }

            } catch (e: Exception) {
                logger.error("MySQL 동기화 실패: promoId=${event.promoId}, userId=${event.userId}", e)
                throw e
            }
        }
    }

    @Transactional("mongoTransactionManager")
    override suspend fun syncToMongo(event: JoinPromotionEvent) {
        try {
            // 1. 먼저 재고 감소 시도 (재고가 0이면 실패)
            val decreaseResult = mongoPort.decreasePromotionStock(event.promoId)
            if (decreaseResult == 0) {
                logger.error("MongoDB 재고 감소 실패 (재고가 이미 0): promoId=${event.promoId}")
                throw Exception("MongoDB 재고가 이미 0인 프로모션에 참여 시도: promoId=${event.promoId}")
            }
            logger.info("MongoDB 재고 감소 완료: promoId=${event.promoId}")

            // 2. 재고 감소 성공 후 참여이력 저장 (유니크 인덱스가 중복을 방지함)
            mongoPort.joinPromotion(event.toDomain())
            logger.info("MongoDB 참여이력 저장 완료: promoId=${event.promoId}, userId=${event.userId}")

        } catch (e: Exception) {
            logger.error("MongoDB 동기화 실패 (트랜잭션 롤백됨): promoId=${event.promoId}, userId=${event.userId}", e)
            throw e
        }
    }
}
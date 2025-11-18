package com.jinjindole2.jinpro.promotion.application.service.web

import com.jinjindole2.jinpro.promotion.application.port.`in`.web.JoinPromotionUseCase
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionCachePort
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionEventPort
import com.jinjindole2.jinpro.promotion.domain.event.JoinPromotionEvent
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.jinpro.promotion.domain.service.PromotionCacheService
import com.jinjindole2.jinpro.common.config.lock.Locker
import com.jinjindole2.jinpro.common.exception.AlreadyJoinedException
import com.jinjindole2.jinpro.common.exception.NotJoinableException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class JoinPromotion (
    private val cachePort: PromotionCachePort,
    private val promotionCacheService: PromotionCacheService,
    private val eventPort: PromotionEventPort,
    private val locker: Locker,
) : JoinPromotionUseCase {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun joinPromotion(promoId: Long, userId: Long, joinDate: LocalDate) {

        // TODO join 이란 문구가 들어가도록 수정. mysql, mongoDB 동기화랑 구분해서 사용해야됨
        locker.lock("lock:promoId:${promoId}userId:${userId}") {

            logger.info("[프로모션 참여 STEP 1] 프로모션 조회 (Cache Aside)")
            val adv = promotionCacheService.findPromotionWithCache(promoId)

            logger.info("[프로모션 참여 STEP 2] 참여가능 기간 확인")
            if (joinDate.isAfter(adv.endDate) || joinDate.isBefore(adv.startDate))
                throw NotJoinableException()

            logger.info("[프로모션 참여 STEP 3] 프로모션 참여 처리 (레디스 루나 스크립트)")
            when (cachePort.joinPromotion(PromotionJoinHistory(promoId = promoId, userId = userId, joinDate = joinDate, rewardAmount = adv.rewardAmount))?: throw Exception("")) {
                -1L -> throw NotJoinableException()
                -2L -> throw Exception("참여제약조건 시스템 오류")
                -3L -> throw NotJoinableException()
                -4L -> throw NotJoinableException()
                -5L -> throw AlreadyJoinedException()
            }

            logger.info("[프로모션 참여 STEP 4] 프로모션 참여 이벤트 발행 (카프카)")
            eventPort.publishJoinPromotionEvent(JoinPromotionEvent(
                userId = userId,
                promoId = promoId,
                joinDate = joinDate,
                rewardAmount = adv.rewardAmount,
                traceId = MDC.get("txid")
            ))
        }
    }
}
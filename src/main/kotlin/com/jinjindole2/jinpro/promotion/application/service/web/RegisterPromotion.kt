package com.jinjindole2.jinpro.promotion.application.service.web

import com.jinjindole2.jinpro.promotion.application.port.`in`.web.RegisterPromotionUseCase
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionEventPort
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionPersistencePort
import com.jinjindole2.jinpro.promotion.domain.event.AddPromotionJoinTypeEvent
import com.jinjindole2.jinpro.promotion.domain.event.RegisterPromotionEvent
import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinType
import com.jinjindole2.jinpro.promotion.domain.model.JoinType
import com.jinjindole2.jinpro.promotion.domain.model.LeadingPromotion
import com.jinjindole2.jinpro.common.exception.PromotionNotFoundException
import com.jinjindole2.jinpro.common.exception.DuplicatedJoinTypeException
import com.jinjindole2.jinpro.common.exception.DuplicatedTitleException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterPromotion (
    private val persistPort: PromotionPersistencePort,
    private val eventPort: PromotionEventPort,
) : RegisterPromotionUseCase {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Transactional("transactionManager")  // MySQL/R2DBC 트랜잭션 매니저
    override suspend fun registerPromotion(adv: Promotion): Promotion {
        logger.info("[프로모션 추가 STEP 1] DB에 저장")
        val registeredAdv = persistPort.registerPromotion(adv) ?: throw DuplicatedTitleException()

        logger.info("[프로모션 추가 STEP 2] 동기화 (Redis, MongoDB)")
        try {
            eventPort.publishRegisterPromotionEvent(RegisterPromotionEvent.fromPromotion(registeredAdv, MDC.get("txid")))
        }catch (e:Exception) {
            // TODO 로그나 잘 남기자
            logger.error(e.stackTraceToString())
        }

        return registeredAdv;
    }

    @Transactional("transactionManager")  // MySQL/R2DBC 트랜잭션 매니저
    override suspend fun addJoinType(promoJoinType: PromotionJoinType) {
        logger.info("[참여조건 추가 STEP 1] 프로모션 등록 여부 확인")
        persistPort.findPromotion(promoJoinType.promoId) ?: throw PromotionNotFoundException()

        // TODO 이걸 빼는게 더 나은거 같은데... 일단 유지
        if (JoinType.LEADING == promoJoinType.joinType) {
            if (promoJoinType.leadPromoIdList.isNullOrEmpty()) throw IllegalArgumentException("선행프로모션목록이 누락되었습니다.")

            if (promoJoinType.leadPromoIdList.contains(promoJoinType.promoId)) throw IllegalArgumentException("자기 자신을 선행프로모션로 등록할 수 없습니다. (id:$promoJoinType.promoId)")

            logger.info("[참여조건 추가 STEP 3-1] 선행프로모션 등록 여부 확인")
            promoJoinType.leadPromoIdList.map {
                persistPort.findPromotion(it) ?: throw IllegalArgumentException("미등록된 선행프로모션ID가 포함되어 있습니다. (id:$it)")
            }

            logger.info("[참여조건 추가 STEP 3] 선행프로모션 조건 추가")
            persistPort.addLeadingPromotionAll(promoJoinType.leadPromoIdList.map {
                LeadingPromotion(
                    leadPromoId=it,
                    trailPromoId=promoJoinType.promoId
                )
            })
        }

        logger.info("[참여조건 추가 STEP 2] 참여조건 추가")
        val savedPromoJoinType = kotlin.runCatching {
            persistPort.addJoinType(promoJoinType)!!
        }.getOrElse {
            throw if (it is DuplicateKeyException) {
                DuplicatedJoinTypeException()
            } else it
        }

        logger.info("[참여조건 추가 STEP 2] 동기화 (Redis, MongoDB)")
        try {
            eventPort.publishAddPromotionJoinTypeEvent(AddPromotionJoinTypeEvent.fromPromotionJoinType(promoJoinType, MDC.get("txid")))
        } catch (e:Exception) {
            // TODO 로그나 잘 남기자
            logger.error(e.stackTraceToString())
        }
    }
}
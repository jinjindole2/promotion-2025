package com.jinjindole2.dailypromotion.promotion.application.service.message

import com.jinjindole2.dailypromotion.promotion.application.port.`in`.message.HandleRewardAssignedEventUseCase
import com.jinjindole2.dailypromotion.promotion.application.port.out.PromotionCachePort
import com.jinjindole2.dailypromotion.promotion.application.port.out.PromotionEventPort
import com.jinjindole2.dailypromotion.promotion.domain.event.JoinPromotionEvent
import com.jinjindole2.dailypromotion.common.config.lock.Locker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HandleRewardAssingedEvent (
    private val cachePort: PromotionCachePort,
    private val eventPort: PromotionEventPort,
    private val locker: Locker,
) : HandleRewardAssignedEventUseCase {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun handleRewardAssignedEvent(event: JoinPromotionEvent) {
        TODO("Not yet implemented")
    }
}
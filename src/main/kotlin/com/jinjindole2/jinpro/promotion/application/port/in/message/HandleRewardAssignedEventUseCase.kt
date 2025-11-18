package com.jinjindole2.jinpro.promotion.application.port.`in`.message

import com.jinjindole2.jinpro.promotion.domain.event.JoinPromotionEvent

interface HandleRewardAssignedEventUseCase {
    suspend fun handleRewardAssignedEvent(event: JoinPromotionEvent)
}
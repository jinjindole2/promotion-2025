package com.jinjindole2.dailypromotion.promotion.application.port.`in`.message

import com.jinjindole2.dailypromotion.promotion.domain.event.JoinPromotionEvent

interface HandleRewardAssignedEventUseCase {
    suspend fun handleRewardAssignedEvent(event: JoinPromotionEvent)
}
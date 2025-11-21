package com.jinjindole2.dailypromotion.promotion.application.port.out

import com.jinjindole2.dailypromotion.promotion.domain.event.AddPromotionJoinTypeEvent
import com.jinjindole2.dailypromotion.promotion.domain.event.JoinPromotionEvent
import com.jinjindole2.dailypromotion.promotion.domain.event.RegisterPromotionEvent

interface PromotionEventPort {
    suspend fun publishRegisterPromotionEvent(event: RegisterPromotionEvent)
    suspend fun publishJoinPromotionEvent(event: JoinPromotionEvent)
    suspend fun publishAddPromotionJoinTypeEvent(fromPromotionJoinType: AddPromotionJoinTypeEvent)
}
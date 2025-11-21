package com.jinjindole2.dailypromotion.promotion.application.port.`in`.web

import java.time.LocalDate

interface JoinPromotionUseCase {
    suspend fun joinPromotion(promoId: Long, userId:Long, joinDate: LocalDate)
}
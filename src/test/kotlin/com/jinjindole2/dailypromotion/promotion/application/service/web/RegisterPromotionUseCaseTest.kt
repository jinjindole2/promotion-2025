package com.jinjindole2.dailypromotion.promotion.application.service.web

import com.jinjindole2.dailypromotion.common.exception.DuplicatedTitleException
import com.jinjindole2.dailypromotion.promotion.application.port.out.PromotionEventPort
import com.jinjindole2.dailypromotion.promotion.application.port.out.PromotionPersistencePort
import com.jinjindole2.dailypromotion.promotion.domain.model.Promotion
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import org.springframework.dao.DuplicateKeyException
import java.time.LocalDate

internal class RegisterPromotionUseCaseTest : StringSpec({

    val persistPort = mockk<PromotionPersistencePort>()
    val eventPort = mockk<PromotionEventPort>()
    val sut = RegisterPromotion(persistPort, eventPort)

    "중복된 프로모션명 등록 시 예외 발생" {
        // Given
        val promotion = Promotion(
            title = "광고1",
            rewardAmount = 10,
            leftJoinCount = 100,
            limitJoinCount = 100,
            content = "광고1 문구",
            imageUrl = "imgUrl",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(1)
        )
        coEvery { persistPort.registerPromotion(promotion) } throws DuplicateKeyException("")

        // When & Then
        shouldThrowExactly<DuplicatedTitleException> {
            sut.registerPromotion(promotion)
        }
    }

    "프로모션 등록 성공" {
        // Given
        val promotion = Promotion(
            title = "광고2",
            rewardAmount = 1000,
            leftJoinCount = 100,
            limitJoinCount = 100,
            content = "광고2 문구",
            imageUrl = "https://example.com/image.jpg",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(7)
        )
        val savedPromotion = promotion.copy(id = 1L)
        coEvery { persistPort.registerPromotion(promotion) } returns savedPromotion
        coJustRun { eventPort.publishRegisterPromotionEvent(any()) }

        // When
        val result = sut.registerPromotion(promotion)

        // Then
        result.shouldNotBeNull()
        result.id.shouldNotBeNull()
        result.id!! shouldBeGreaterThan 0L
        result.title shouldBe "광고2"
        result.rewardAmount shouldBe 1000
        result.limitJoinCount shouldBe 100
    }
})


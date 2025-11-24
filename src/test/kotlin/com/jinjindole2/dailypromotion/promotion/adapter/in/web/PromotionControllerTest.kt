package com.jinjindole2.dailypromotion.promotion.adapter.`in`.web

import com.jinjindole2.dailypromotion.common.exception.DuplicatedTitleException
import com.jinjindole2.dailypromotion.common.exception.GlobalExceptionHandler
import com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto.RegisterPromotionReqDto
import com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.mapper.PromotionWebMapperImpl
import com.jinjindole2.dailypromotion.promotion.application.port.`in`.web.FindJoinablePromotionUseCase
import com.jinjindole2.dailypromotion.promotion.application.port.`in`.web.FindPromotionJoinHistoryUseCase
import com.jinjindole2.dailypromotion.promotion.application.port.`in`.web.JoinPromotionUseCase
import com.jinjindole2.dailypromotion.promotion.application.port.`in`.web.RegisterPromotionUseCase
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate

internal class PromotionControllerTest : StringSpec({

    val findJoinablePromotionUseCase = mockk<FindJoinablePromotionUseCase>()
    val findPromotionJoinHistoryUseCase = mockk<FindPromotionJoinHistoryUseCase>()
    val joinPromotionUseCase = mockk<JoinPromotionUseCase>()
    val registerPromotionUseCase = mockk<RegisterPromotionUseCase>()
    val webMapper = PromotionWebMapperImpl()

    val controller = PromotionController(
        findJoinablePromotionUseCase,
        findPromotionJoinHistoryUseCase,
        joinPromotionUseCase,
        registerPromotionUseCase,
        webMapper
    )

    val webTestClient = WebTestClient.bindToController(controller)
        .controllerAdvice(GlobalExceptionHandler())
        .build()

    "중복된 프로모션명 등록 시 409 Conflict 응답" {
        // Given
        val request = RegisterPromotionReqDto(
            title = "중복 프로모션",
            rewardAmount = 1000,
            limitJoinCount = 100,
            content = "중복 프로모션 내용",
            imageUrl = "https://example.com/image.jpg",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(7)
        )

        coEvery { registerPromotionUseCase.registerPromotion(any()) } throws DuplicatedTitleException()

        // When & Then
        webTestClient.post()
            .uri("/promotion/")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    "프로모션명이 빈값일 때 400 Bad Request 응답" {
        // Given
        val request = RegisterPromotionReqDto(
            title = "",  // 빈값
            rewardAmount = 1000,
            limitJoinCount = 100,
            content = "내용",
            imageUrl = "https://example.com/image.jpg",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(7)
        )

        // When & Then
        webTestClient.post()
            .uri("/promotion/")
            .header("Host", "localhost:8080")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
    }

    "적립금액이 음수일 때 400 Bad Request 응답" {
        // Given
        val request = RegisterPromotionReqDto(
            title = "프로모션",
            rewardAmount = -100,  // 음수
            limitJoinCount = 100,
            content = "내용",
            imageUrl = "https://example.com/image.jpg",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(7)
        )

        // When & Then
        webTestClient.post()
            .uri("/promotion/")
            .header("Host", "localhost:8080")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
    }

    "참여가능횟수가 0 이하일 때 400 Bad Request 응답" {
        // Given
        val request = RegisterPromotionReqDto(
            title = "프로모션",
            rewardAmount = 1000,
            limitJoinCount = 0,  // 0
            content = "내용",
            imageUrl = "https://example.com/image.jpg",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(7)
        )

        // When & Then
        webTestClient.post()
            .uri("/promotion/")
            .header("Host", "localhost:8080")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
    }

    "정상적인 프로모션 등록 시 201 Created 응답" {
        // Given
        val request = RegisterPromotionReqDto(
            title = "신규 프로모션",
            rewardAmount = 1000,
            limitJoinCount = 100,
            content = "신규 프로모션 내용",
            imageUrl = "https://example.com/image.jpg",
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(7)
        )

        // webMapper.registReqToEntity(request)가 실제로 변환한 결과를 사용
        val promotion = webMapper.registReqToEntity(request)
        val savedPromotion = promotion.copy(id = 1L)

        coEvery { registerPromotionUseCase.registerPromotion(any()) } returns savedPromotion

        // When & Then
        webTestClient.post()
            .uri("/promotion/")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().location("/promotion/1")
    }
})
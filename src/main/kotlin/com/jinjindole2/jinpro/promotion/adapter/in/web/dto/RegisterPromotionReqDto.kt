package com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto

import com.jinjindole2.jinpro.common.validate.annotation.DateRange
import com.jinjindole2.jinpro.common.validate.annotation.DateType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDate

//@Schema(description = "등록할 프로모션 객체")
//@ValidDateRange
data class RegisterPromotionReqDto (

    @Schema(description = "프로모션명", example = "프로모션명1")
    @field:NotBlank(message = "프로모션명이 존재하지 않습니다.")
    val title : String,

    @Schema(description = "적립금액", example = "1000")
    @field:NotNull(message = "적립금액이 존재하지 않습니다.")
    @field:PositiveOrZero(message = "적립금액은 0 이상이여야 합니다")
    val rewardAmount : Long,

    @Schema(description = "프로모션 참여 가능 횟수", example = "10")
    @field:NotNull(message = "프로모션 참여 가능 횟수가 존재하지 않습니다.")
    @field:Positive(message = "프로모션 참여 가능 횟수는 0보다 커야합니다")
    val limitJoinCount : Long,

    @Schema(description = "내용", example = "첫번째 프로모션 내용입니다.")
    val content : String?,

    @Schema(description = "이미지 url", example = "https://jinjindole2.com/image")
    val imageUrl : String?,

    @Schema(description = "시작일자(YYYYMMDD)", example = "20240101")
    @DateRange(type = DateType.START)
    @field:NotNull(message = "시작일자(YYYYMMDD)이 존재하지 않습니다.")
    val startDate : LocalDate,

    @Schema(description = "완료일자(YYYYMMDD)", example = "20240201")
    @DateRange(type = DateType.END)
    @field:NotNull(message = "완료일자(YYYYMMDD)이 존재하지 않습니다.")
    val endDate : LocalDate,
)
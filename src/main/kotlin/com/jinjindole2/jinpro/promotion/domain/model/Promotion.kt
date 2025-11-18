package com.jinjindole2.jinpro.promotion.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 헥사고날 아키텍처 개선: 도메인 모델을 순수하게 유지
 * - 인프라 계층 의존성 제거
 * - 도메인 로직에만 집중
 */
class Promotion @JsonCreator constructor(
    @JsonProperty("id") val id: Long? = null,
    @JsonProperty("title") val title: String,
    @JsonProperty("rewardAmount") val rewardAmount: Long,
    @JsonProperty("leftJoinCount") val leftJoinCount: Long,
    @JsonProperty("limitJoinCount") val limitJoinCount: Long,
    @JsonProperty("content") val content: String?,
    @JsonProperty("imageUrl") val imageUrl: String?,
    @JsonProperty("startDate") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") val startDate: LocalDate,
    @JsonProperty("endDate") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") val endDate: LocalDate,
    @JsonProperty("createdDate") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") val createdDate: LocalDateTime? = null,
    @JsonProperty("lastModifiedDate") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") val lastModifiedDate: LocalDateTime? = null
) {

    // 도메인 비즈니스 로직만 포함
    fun isJoinable(currentDate: LocalDate): Boolean {
        return currentDate.isAfter(startDate.minusDays(1)) &&
               currentDate.isBefore(endDate.plusDays(1)) &&
               leftJoinCount > 0
    }

    fun hasJoinCountAvailable(): Boolean = leftJoinCount > 0

    fun isWithinJoinPeriod(joinDate: LocalDate): Boolean {
        return !joinDate.isBefore(startDate) && !joinDate.isAfter(endDate)
    }

    fun decreaseJoinCount(): Promotion {
        require(leftJoinCount > 0) { "참여 가능한 횟수가 없습니다." }
        return this.copy(leftJoinCount = leftJoinCount - 1)
    }

    fun copy(
        id: Long? = this.id,
        title: String = this.title,
        rewardAmount: Long = this.rewardAmount,
        leftJoinCount: Long = this.leftJoinCount,
        limitJoinCount: Long = this.limitJoinCount,
        content: String? = this.content,
        imageUrl: String? = this.imageUrl,
        startDate: LocalDate = this.startDate,
        endDate: LocalDate = this.endDate,
        createdDate: LocalDateTime? = this.createdDate,
        lastModifiedDate: LocalDateTime? = this.lastModifiedDate
    ): Promotion {
        return Promotion(
            id, title, rewardAmount, leftJoinCount, limitJoinCount,
            content, imageUrl, startDate, endDate, createdDate, lastModifiedDate
        )
    }

    companion object {
        fun create(
            title: String,
            rewardAmount: Long,
            limitJoinCount: Long,
            content: String?,
            imageUrl: String?,
            startDate: LocalDate,
            endDate: LocalDate
        ): Promotion {
            require(title.isNotBlank()) { "프로모션명은 필수입니다." }
            require(rewardAmount > 0) { "적립 금액은 0보다 커야 합니다." }
            require(limitJoinCount > 0) { "참여 가능 횟수는 0보다 커야 합니다." }
            require(!startDate.isAfter(endDate)) { "시작일은 종료일보다 늦을 수 없습니다." }

            return Promotion(
                title = title,
                rewardAmount = rewardAmount,
                leftJoinCount = limitJoinCount, // 초기값은 제한 횟수와 동일
                limitJoinCount = limitJoinCount,
                content = content,
                imageUrl = imageUrl,
                startDate = startDate,
                endDate = endDate
            )
        }
    }
}
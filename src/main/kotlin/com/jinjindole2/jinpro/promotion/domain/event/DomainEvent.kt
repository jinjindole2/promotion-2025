package com.jinjindole2.jinpro.promotion.domain.event

import java.time.Instant
import java.util.UUID

/**
 * 도메인 이벤트의 공통 메타데이터를 정의하는 기반 클래스
 */
abstract class DomainEvent(
    open val eventId: String = UUID.randomUUID().toString(),
    open val eventType: String,
    open val producedAt: Instant = Instant.now(),
    open val source: String = "promotion-service",
    open val version: Int = 1,
    open val traceId: String? = null
) {
    /**
     * 이벤트 처리를 위한 공통 메서드들
     */
    fun getTraceIdOrDefault(): String = traceId ?: "NO-TRACE"

    fun isTraceEnabled(): Boolean = !traceId.isNullOrBlank()

    /**
     * 로그용 이벤트 정보 문자열 생성
     */
    fun toLogString(): String {
        return "Event[type=$eventType, id=$eventId, traceId=${getTraceIdOrDefault()}, source=$source, version=$version, producedAt=$producedAt]"
    }
}
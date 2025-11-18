package com.jinjindole2.jinpro.common.logging

import org.slf4j.MDC
import com.jinjindole2.jinpro.promotion.domain.event.DomainEvent

/**
 * 분산 추적을 위한 TraceId 관리 유틸리티
 */
object TraceContextManager {
    private const val TRACE_ID_KEY = "traceId"
    private const val EVENT_TYPE_KEY = "eventType"
    private const val EVENT_ID_KEY = "eventId"
    
    /**
     * 이벤트의 traceId를 MDC에 설정
     */
    fun setTraceContext(event: DomainEvent) {
        val traceId = event.getTraceIdOrDefault()
        MDC.put(TRACE_ID_KEY, traceId)
        MDC.put(EVENT_TYPE_KEY, event.eventType)
        MDC.put(EVENT_ID_KEY, event.eventId)
    }
    
    /**
     * MDC에서 traceId 정보 제거
     */
    fun clearTraceContext() {
        MDC.remove(TRACE_ID_KEY)
        MDC.remove(EVENT_TYPE_KEY)
        MDC.remove(EVENT_ID_KEY)
    }
    
    /**
     * 현재 MDC의 traceId 반환
     */
    fun getCurrentTraceId(): String? {
        return MDC.get(TRACE_ID_KEY)
    }
    
    /**
     * traceId와 함께 작업 실행
     */
    inline fun <T> withTraceContext(event: DomainEvent, action: () -> T): T {
        return try {
            setTraceContext(event)
            action()
        } finally {
            clearTraceContext()
        }
    }
    
    /**
     * 수동으로 traceId 설정
     */
    fun setTraceId(traceId: String) {
        MDC.put(TRACE_ID_KEY, traceId)
    }
}
package com.jinjindole2.dailypromotion.common.exception

import com.jinjindole2.dailypromotion.common.filter.LoggingFilter
import com.jinjindole2.dailypromotion.common.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import java.nio.charset.StandardCharsets

/**
 * Fastcampus 방식의 GlobalExceptionHandler
 * Controller에서 변환된 HTTP 예외를 응답으로 포맷팅
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    /**
     * HTTP 예외를 응답으로 포맷팅 (단일 핸들러)
     * Controller에서 이미 ErrorCodes와 HttpStatus가 결정된 상태
     * customMessage가 있으면 ErrorCodes의 기본 메시지 대신 사용
     */
    @ExceptionHandler(CommonPromotionHttpException::class)
    fun handleCommonPromotionHttpException(
        exception: CommonPromotionHttpException
    ): ResponseEntity<ApiResponse<Void>> {
        logger.error(exception.stackTraceToString())

        val body = if (exception.customMessage != null) {
            // 커스텀 메시지가 있으면 사용
            ApiResponse.fromErrorCodes<Void>(exception.errorCodes, exception.customMessage)
        } else {
            // 없으면 ErrorCodes의 기본 메시지 사용
            ApiResponse.fromErrorCodes<Void>(exception.errorCodes)
        }

        val contentType = MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)

        return ResponseEntity.status(exception.httpStatus)
            .contentType(contentType)
            .body(body)
    }

    /**
     * 유효성 검사 예외 처리
     */
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(
        exception: WebExchangeBindException
    ): ResponseEntity<ApiResponse<Void>> {
        logger.error(exception.stackTraceToString())

        val body = ApiResponse.fromErrorCodes<Void>(ErrorCodes.INVALID_INPUT)
        val contentType = MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)

        return ResponseEntity.badRequest()
            .contentType(contentType)
            .body(body)
    }

    /**
     * 시스템 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        exception: Exception
    ): ResponseEntity<ApiResponse<Void>> {
        logger.error(exception.stackTraceToString())

        val body = ApiResponse.fromErrorCodes<Void>(ErrorCodes.SYSTEM_ERROR)
        val contentType = MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)

        return ResponseEntity.internalServerError()
            .contentType(contentType)
            .body(body)
    }
}

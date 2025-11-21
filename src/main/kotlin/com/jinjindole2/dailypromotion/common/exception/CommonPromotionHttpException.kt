package com.jinjindole2.dailypromotion.common.exception

import org.springframework.http.HttpStatus

/**
 * HTTP 레이어 전용 예외 클래스
 * 서비스 레이어 예외를 HTTP 응답으로 변환할 때 사용
 *
 * 사용 방법:
 * 1. 기본 메시지 사용: CommonPromotionHttpException(ErrorCodes.INVALID_INPUT, HttpStatus.BAD_REQUEST)
 * 2. 커스텀 메시지 사용: CommonPromotionHttpException(ErrorCodes.INVALID_INPUT, HttpStatus.BAD_REQUEST, "페이지는 0 이상이어야 합니다.")
 */
class CommonPromotionHttpException : RuntimeException {
    val errorCodes: ErrorCodes
    val httpStatus: HttpStatus
    val customMessage: String?

    /**
     * 기본 메시지 사용 (ErrorCodes의 message 사용)
     */
    constructor(
        errorCodes: ErrorCodes,
        httpStatus: HttpStatus
    ) : super(errorCodes.message) {
        this.errorCodes = errorCodes
        this.httpStatus = httpStatus
        this.customMessage = null
    }

    /**
     * 커스텀 메시지 사용 (동적 메시지 전달)
     */
    constructor(
        errorCodes: ErrorCodes,
        httpStatus: HttpStatus,
        customMessage: String
    ) : super(customMessage) {
        this.errorCodes = errorCodes
        this.httpStatus = httpStatus
        this.customMessage = customMessage
    }
}

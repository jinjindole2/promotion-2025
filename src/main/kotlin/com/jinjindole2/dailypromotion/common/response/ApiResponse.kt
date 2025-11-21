package com.jinjindole2.dailypromotion.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.jinjindole2.dailypromotion.common.exception.ErrorCodes

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val data: T? = null,
    val error: ApiErrorResponse? = null
) {
    companion object {
        fun <T> just(data: T): ApiResponse<T> {
            return ApiResponse(data = data, error = null)
        }

        /**
         * ErrorCodes의 기본 메시지 사용
         */
        fun <T> fromErrorCodes(errorCodes: ErrorCodes): ApiResponse<T> {
            val errorResponse = ApiErrorResponse(
                message = errorCodes.message,
                code = errorCodes.code
            )
            return ApiResponse(data = null, error = errorResponse)
        }

        /**
         * 커스텀 메시지 사용 (동적 메시지)
         */
        fun <T> fromErrorCodes(errorCodes: ErrorCodes, customMessage: String): ApiResponse<T> {
            val errorResponse = ApiErrorResponse(
                message = customMessage,
                code = errorCodes.code
            )
            return ApiResponse(data = null, error = errorResponse)
        }
    }
}

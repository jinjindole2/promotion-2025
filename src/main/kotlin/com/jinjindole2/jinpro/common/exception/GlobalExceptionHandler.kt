package com.jinjindole2.jinpro.common.exception

import com.jinjindole2.jinpro.common.filter.LoggingFilter
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.support.WebExchangeBindException

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    // 100	유효성 검사
    @ExceptionHandler(InvalidInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleInvalidInputException(ex: InvalidInputException): ResponseEntity<Map<String, Any>> {
        logger.error(ex.stackTraceToString())
        val response = mapOf("code" to 100, "message" to ex.message)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    // 100	유효성 검사
    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleWebExchangeBindException(ex: WebExchangeBindException): ResponseEntity<Map<String, Any>> {
        logger.error(ex.stackTraceToString())
        val firstError = ex.bindingResult.allErrors.firstOrNull()
        val errorMessage = firstError?.defaultMessage?: "시스템 오류"
        val response = mapOf("code" to HttpStatus.BAD_REQUEST.value(), "message" to errorMessage)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    // 200	미등록 프로모션ID 입니다.
    @ExceptionHandler(PromotionNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handlePromotionNotFoundException(ex: PromotionNotFoundException): ResponseEntity<Map<String, Any>> {
        logger.error(ex.stackTraceToString())
        val response = mapOf("code" to ex.code, "message" to ex.message)
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    // 201	중복된 프로모션명이 존재합니다.
    @ExceptionHandler(DuplicatedTitleException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun handleDuplicatedTitleException(request: ServerHttpRequest, ex: DuplicatedTitleException): ResponseEntity<Map<String, Any>> {
        MDC.put("txid", request.headers.get("x-txid").toString())
        logger.error(ex.stackTraceToString())
        val response = mapOf("code" to ex.code, "message" to ex.message)
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    // 300	미등록 선행 프로모션ID 입니다.
    @ExceptionHandler(LeadingPromotionNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleLeadingPromotionNotFoundException(ex: LeadingPromotionNotFoundException): ResponseEntity<Map<String, Any>> {
        logger.error(ex.stackTraceToString())
        val response = mapOf("code" to ex.code, "message" to ex.message)
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    // 301	기등록된 참여제한조건 입니다.
    @ExceptionHandler(DuplicatedJoinTypeException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun handleDuplicatedJoinTypeException(ex: DuplicatedJoinTypeException): ResponseEntity<Map<String, Any>> {
        logger.error(ex.stackTraceToString())
        val response = mapOf("code" to ex.code, "message" to ex.message)
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    // 302	기등록된 선행프로모션 입니다.
    @ExceptionHandler(DuplicatedLeadingPromotionException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun handleDuplicatedLeadingPromotionException(ex: DuplicatedLeadingPromotionException): ResponseEntity<Map<String, Any>> {
        logger.error(ex.stackTraceToString())
        val response = mapOf("code" to ex.code, "message" to ex.message)
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    // 400	참여할 수 없는 프로모션 입니다.
    @ExceptionHandler(NotJoinableException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    fun handleNotJoinableException(ex: NotJoinableException): ResponseEntity<Map<String, Any>> {
        logger.error(ex.stackTraceToString())
        val response = mapOf("code" to ex.code, "message" to ex.message)
        return ResponseEntity(response, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    // 401	이미 참여한 프로모션 입니다.
    @ExceptionHandler(AlreadyJoinedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun handleAlreadyJoinedException(ex: AlreadyJoinedException): ResponseEntity<Map<String, Any>> {
        logger.error(ex.stackTraceToString())
        val response = mapOf("code" to ex.code, "message" to ex.message)
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    // 999	시스템 오류
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, Any>> {
        logger.error(ex.stackTraceToString())
        val response = mapOf("code" to HttpStatus.INTERNAL_SERVER_ERROR.value(), "message" to "시스템오류")
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

package com.jinjindole2.dailypromotion.common.exception

enum class ErrorCodes(
    val message: String,
    val code: Long
) {
    // 100번대: 유효성 검사
    INVALID_INPUT("유효하지 않은 입력값입니다.", 100L),

    // 200번대: 프로모션 관련
    PROMOTION_NOT_FOUND("미등록 프로모션ID 입니다.", 200L),
    DUPLICATED_TITLE("중복된 프로모션명이 존재합니다.", 201L),

    // 300번대: 선행 프로모션 및 참여 제한 조건
    LEADING_PROMOTION_NOT_FOUND("미등록 선행 프로모션ID 입니다.", 300L),
    DUPLICATED_JOIN_TYPE("기등록된 참여제한조건 입니다.", 301L),
    DUPLICATED_LEADING_PROMOTION("기등록된 선행프로모션 입니다.", 302L),

    // 400번대: 프로모션 참여
    NOT_JOINABLE("참여할 수 없는 프로모션 입니다.", 400L),
    ALREADY_JOINED("이미 참여한 프로모션 입니다.", 401L),

    // 999: 시스템 오류
    SYSTEM_ERROR("시스템 오류", 999L)
}

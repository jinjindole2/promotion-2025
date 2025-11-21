package com.jinjindole2.dailypromotion.common.exception

/**
 * 서비스 레이어 전용 예외 클래스들
 * HTTP 관련 정보는 포함하지 않으며, 순수 비즈니스 로직 예외만 표현
 * Fastcampus 방식: 일반 RuntimeException을 상속
 */

// 100번대: 유효성 검사
class InvalidInputException(message : String?) : RuntimeException(message ?: "유효하지 않은 입력입니다.")

// 200번대: 프로모션 관련
class PromotionNotFoundException : RuntimeException()

class DuplicatedTitleException : RuntimeException()

// 300번대: 선행 프로모션 및 참여 제한 조건
class LeadingPromotionNotFoundException : RuntimeException()

class DuplicatedJoinTypeException : RuntimeException()

class DuplicatedLeadingPromotionException : RuntimeException()

// 400번대: 프로모션 참여
class NotJoinableException : RuntimeException()

class AlreadyJoinedException : RuntimeException()

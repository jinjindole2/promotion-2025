package com.jinjindole2.jinpro.common.exception

open class PromotionException(
    val code: Int,
    override val message: String
) : RuntimeException(message)


// 100	입력값이 유효하지 않습니다.
class InvalidInputException(
    message: String = "유효하지 않은 입력값입니다."
) : PromotionException(100, message)

// 200	미등록 프로모션ID 입니다.
class PromotionNotFoundException(
    message: String = "미등록 프로모션ID 입니다."
) : PromotionException(200, message)

// 201	중복된 프로모션명이 존재합니다.
class DuplicatedTitleException(
    message: String = "중복된 프로모션명이 존재합니다."
) : PromotionException(201, message)

// 300	미등록 선행 프로모션ID 입니다.
class LeadingPromotionNotFoundException(
    message: String = "미등록 선행 프로모션ID 입니다."
) : PromotionException(300, message)

// 301	기등록된 참여제한조건 입니다.
class DuplicatedJoinTypeException(
    message: String = "기등록된 참여제한조건 입니다."
) : PromotionException(301, message)

// 302	기등록된 선행프로모션 입니다.
class DuplicatedLeadingPromotionException(
    message: String = "기등록된 선행프로모션 입니다."
) : PromotionException(302, message)

// 400	참여할 수 없는 프로모션 입니다.
class NotJoinableException(
    message: String = "참여할 수 없는 프로모션 입니다."
) : PromotionException(400, message)

// 401	이미 참여한 프로모션 입니다.
class AlreadyJoinedException(
    message: String = "이미 참여한 프로모션 입니다."
) : PromotionException(401, message)

// 999	시스템 오류
class ServiceException(
    message: String = "시스템 오류"
) : PromotionException(999, message)
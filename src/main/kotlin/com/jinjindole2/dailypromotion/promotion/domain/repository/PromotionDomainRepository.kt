package com.jinjindole2.dailypromotion.promotion.domain.repository

import com.jinjindole2.dailypromotion.promotion.domain.model.Promotion
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinType

/**
 * 헥사고날 아키텍처 개선: 도메인 레포지토리 인터페이스
 * - 도메인 계층에서 정의하는 순수한 인터페이스
 * - 도메인 용어로 표현된 메서드명
 * - 인프라 기술에 독립적
 */
interface PromotionDomainRepository {

    /**
     * 프로모션 저장
     */
    suspend fun save(promotion: Promotion): Promotion

    /**
     * 프로모션 조회
     */
    suspend fun findById(id: Long): Promotion?

    /**
     * 제목으로 프로모션 조회 (중복 검증용)
     */
    suspend fun findByTitle(title: String): Promotion?

    /**
     * 모든 프로모션 제목 조회 (중복 검증용)
     */
    suspend fun findAllTitles(): List<String>

    /**
     * 참여 가능한 프로모션 조회
     * @param userId 사용자 ID
     * @param limit 조회 제한 개수
     * @param offset 오프셋
     */
    suspend fun findJoinablePromotions(userId: Long, limit: Long, offset: Long): List<Promotion>

    /**
     * 참여 가능한 프로모션 총 개수
     */
    suspend fun countJoinablePromotions(userId: Long): Long
}

/**
 * 프로모션 참여 이력 도메인 레포지토리
 */
interface PromotionJoinHistoryDomainRepository {

    /**
     * 참여 이력 저장
     */
    suspend fun save(joinHistory: PromotionJoinHistory): PromotionJoinHistory

    /**
     * 사용자의 프로모션 참여 이력 조회
     */
    suspend fun findByUserId(userId: Long, limit: Long, offset: Long): List<PromotionJoinHistory>

    /**
     * 사용자의 특정 프로모션 참여 이력 조회
     */
    suspend fun findByUserIdAndpromoId(userId: Long, promoId: Long): List<PromotionJoinHistory>

    /**
     * 사용자의 참여 이력 총 개수
     */
    suspend fun countByUserId(userId: Long): Long

    /**
     * 사용자가 특정 프로모션에 특정 날짜에 참여했는지 확인
     */
    suspend fun existsByUserIdAndpromoIdAndJoinDate(userId: Long, promoId: Long, joinDate: java.time.LocalDate): Boolean
}

/**
 * 프로모션 참여 타입 도메인 레포지토리
 */
interface PromotionJoinTypeDomainRepository {

    /**
     * 참여 타입 저장
     */
    suspend fun save(joinType: PromotionJoinType): PromotionJoinType

    /**
     * 프로모션의 참여 타입 조회
     */
    suspend fun findBypromoId(promoId: Long): List<PromotionJoinType>

    /**
     * 특정 참여 타입 조회
     */
    suspend fun findBypromoIdAndJoinType(promoId: Long, joinType: com.jinjindole2.dailypromotion.promotion.domain.model.JoinType): PromotionJoinType?
}
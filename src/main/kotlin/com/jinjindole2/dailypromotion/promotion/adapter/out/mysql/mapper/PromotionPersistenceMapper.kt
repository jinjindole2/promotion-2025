package com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.mapper

import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.entity.PromotionEntity
import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.entity.PromotionJoinHistoryEntity
import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.entity.PromotionJoinTypeEntity
import com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.entity.LeadingPromotionEntity
import com.jinjindole2.dailypromotion.promotion.domain.model.Promotion
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.dailypromotion.promotion.domain.model.PromotionJoinType
import com.jinjindole2.dailypromotion.promotion.domain.model.LeadingPromotion
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * 헥사고날 아키텍처 개선: 어댑터 계층에서만 매핑 담당
 * - 도메인 모델 ↔ 퍼시스턴스 엔티티 변환
 * - 어댑터 패키지 내에 위치하여 의존성 방향 준수
 */
@Mapper(componentModel = "spring")
interface PromotionPersistenceMapper {

    //@Mapping(target = "id", ignore = true)
    //@Mapping(target = "createdDate", ignore = true)
    //@Mapping(target = "lastModifiedDate", ignore = true)
    fun domainToEntity(domain: Promotion): PromotionEntity

    fun entityToDomain(entity: PromotionEntity): Promotion

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    fun domainToEntity(domain: PromotionJoinHistory): PromotionJoinHistoryEntity

    fun entityToDomain(entity: PromotionJoinHistoryEntity): PromotionJoinHistory

    @Mapping(target = "id", ignore = true)
    fun domainToEntity(domain: PromotionJoinType): PromotionJoinTypeEntity

    fun entityToDomain(entity: PromotionJoinTypeEntity): PromotionJoinType

    @Mapping(target = "id", ignore = true)
    fun domainToEntity(domain: LeadingPromotion): LeadingPromotionEntity

    fun entityToDomain(entity: LeadingPromotionEntity): LeadingPromotion
}
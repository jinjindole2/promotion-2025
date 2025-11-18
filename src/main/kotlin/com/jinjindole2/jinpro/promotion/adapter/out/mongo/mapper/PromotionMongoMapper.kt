package com.jinjindole2.jinpro.promotion.adapter.out.mongo.mapper

import com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.document.PromotionDocument
import com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.document.PromotionJoinHistoryDocument
import com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.document.PromotionJoinTypeDocument
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionResDto
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionJoinHistoryResDto
import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinType
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * 헥사고날 아키텍처 개선: MongoDB 어댑터용 매퍼
 * - 도메인 모델 ↔ MongoDB 도큐먼트 변환
 * - 조회용 DTO 변환도 함께 처리
 */
@Mapper(componentModel = "spring")
interface PromotionMongoMapper {

    @Mapping(target = "objectId", ignore = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "joinTypes", ignore = true)
    fun domainToDocument(domain: Promotion): PromotionDocument

    @Mapping(target = "id", source = "id")
    fun documentToDomain(document: PromotionDocument): Promotion

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "promoId", expression = "java(String.valueOf(domain.getPromoId()))")
    @Mapping(target = "userId", expression = "java(String.valueOf(domain.getUserId()))")
    fun domainToDocument(domain: PromotionJoinHistory): PromotionJoinHistoryDocument

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "promoId", expression = "java(Long.valueOf(document.getPromoId()))")
    @Mapping(target = "userId", expression = "java(Long.valueOf(document.getUserId()))")
    fun documentToDomain(document: PromotionJoinHistoryDocument): PromotionJoinHistory

    //@Mapping(target = "promoId", ignore = true)
    fun domainToDocument(domain: PromotionJoinType): PromotionJoinTypeDocument

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "promoId", ignore = true)
    fun documentToDomain(document: PromotionJoinTypeDocument): PromotionJoinType

    // 조회용 DTO 변환
    fun documentToResDto(document: PromotionDocument): FindPromotionResDto

    @Mapping(target = "title", ignore = true)
    fun documentToResDto(document: PromotionJoinHistoryDocument): FindPromotionJoinHistoryResDto

}
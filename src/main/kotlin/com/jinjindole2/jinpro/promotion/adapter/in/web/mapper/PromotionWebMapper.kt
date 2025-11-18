package com.jinjindole2.jinpro.promotion.adapter.`in`.web.mapper

import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.AddPromotionJoinTypeReqDto
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.JoinPromotionReqDto
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.RegisterPromotionReqDto
import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinType
import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinHistory
import com.jinjindole2.jinpro.promotion.domain.model.LeadingPromotion
import org.mapstruct.Mapper
import org.mapstruct.Mapping
/**
 * 헥사고날 아키텍처 개선: 웹 어댑터용 매퍼
 * - 웹 요청 DTO ↔ 도메인 모델 변환
 * - 웹 어댑터 패키지 내에 위치
 */
@Mapper(componentModel = "spring")
interface PromotionWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(source = "limitJoinCount", target = "leftJoinCount")
    fun registReqToEntity(request: RegisterPromotionReqDto): Promotion

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "rewardAmount", ignore = true)
    @Mapping(target = "joinDate", expression = "java(java.time.LocalDate.now())")
    fun joinReqToEntity(request: JoinPromotionReqDto): PromotionJoinHistory

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    fun addJoinTypeReqToEntity(request: AddPromotionJoinTypeReqDto): PromotionJoinType

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(source = "promoId", target = "trailPromoId")
    fun addJoinTypeReqToLeadingEntity(request: AddPromotionJoinTypeReqDto): LeadingPromotion
}
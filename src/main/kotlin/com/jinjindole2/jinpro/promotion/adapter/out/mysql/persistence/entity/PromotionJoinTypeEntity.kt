package com.jinjindole2.jinpro.promotion.adapter.out.mysql.persistence.entity

import com.jinjindole2.jinpro.promotion.domain.model.PromotionJoinType
import com.jinjindole2.jinpro.promotion.domain.model.JoinType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("promotion_join_type")
class PromotionJoinTypeEntity(
    @Id
    val id : Long? = null,
    @Column
    val promoId : Long,
    @Column
    val joinType: JoinType,
    @Column
    val limitDupJoinCount: Long? = null,
    @Column
    @CreatedDate
    var createdDate : LocalDateTime? = null,
    @Column
    @LastModifiedDate
    var lastModifiedDate : LocalDateTime? = null,
) {
    fun toDomain() = PromotionJoinType(
        promoId=promoId,
        joinType=joinType,
        limitDupJoinCount=limitDupJoinCount,
        createdDate=createdDate,
        lastModifiedDate = lastModifiedDate
    )
}
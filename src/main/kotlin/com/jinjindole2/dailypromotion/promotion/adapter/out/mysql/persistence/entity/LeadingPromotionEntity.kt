package com.jinjindole2.dailypromotion.promotion.adapter.out.mysql.persistence.entity

import com.jinjindole2.dailypromotion.promotion.domain.model.LeadingPromotion
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("leading_promotion")
class LeadingPromotionEntity(
    @Id
    val id : Long? = null,
    @Column
    val leadPromoId : Long,
    @Column
    val trailPromoId : Long,
    @Column
    @CreatedDate
    var createdDate : LocalDateTime? = null,
    @Column
    @LastModifiedDate
    var lastModifiedDate : LocalDateTime? = null,
) {
    fun toDomain() = LeadingPromotion(
        leadPromoId, trailPromoId, createdDate, lastModifiedDate
    )
}
package com.jinjindole2.jinpro.promotion.adapter.out.mysql.persistence.entity

import com.jinjindole2.jinpro.promotion.domain.model.Promotion
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("promotion")
class PromotionEntity (
    @Id
    val id : Long? = null,
    @Column
    var title : String,
    @Column
    var rewardAmount : Long,
    @Column
    var leftJoinCount : Long,
    @Column
    var limitJoinCount : Long,
    @Column
    var content : String?,
    @Column
    var imageUrl : String?,
    @Column
    var startDate : LocalDate,
    @Column
    var endDate : LocalDate,
    @Column
    @CreatedDate
    var createdDate : LocalDateTime? = null,
    @Column
    @LastModifiedDate
    var lastModifiedDate : LocalDateTime? = null,
) {
    fun toDomain() = Promotion(
        id, title, rewardAmount, leftJoinCount, limitJoinCount, content, imageUrl, startDate, endDate, createdDate, lastModifiedDate
    )
}
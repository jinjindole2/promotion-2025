package com.jinjindole2.jinpro.promotion.adapter.out.mysql.persistence.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("promotion_join_history")
class PromotionJoinHistoryEntity(
    @Id
    val id : Long? = null,
    @Column
    val promoId : Long,
    @Column
    val userId : Long,
    @Column
    val joinDate : LocalDate,
    @Column
    var rewardAmount : Long,
    @Column
    @CreatedDate
    var createdDate : LocalDateTime? = null,
    @Column
    @LastModifiedDate
    var lastModifiedDate : LocalDateTime? = null,
)
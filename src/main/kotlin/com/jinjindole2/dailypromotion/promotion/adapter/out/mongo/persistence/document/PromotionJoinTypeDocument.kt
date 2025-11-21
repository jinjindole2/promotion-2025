package com.jinjindole2.dailypromotion.promotion.adapter.out.mongo.persistence.document

import com.jinjindole2.dailypromotion.promotion.domain.model.JoinType

data class PromotionJoinTypeDocument(
    val joinType: JoinType,
    val limitDupJoinCount: Long? = null,
    val leadPromoIdList : List<String>? = null
)
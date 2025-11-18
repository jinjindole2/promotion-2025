package com.jinjindole2.jinpro.promotion.adapter.out.mongo.persistence.document

import com.jinjindole2.jinpro.promotion.domain.model.JoinType

data class PromotionJoinTypeDocument(
    val joinType: JoinType,
    val limitDupJoinCount: Long? = null,
    val leadPromoIdList : List<String>? = null
)
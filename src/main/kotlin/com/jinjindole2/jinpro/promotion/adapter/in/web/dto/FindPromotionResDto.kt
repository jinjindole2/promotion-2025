package com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto

data class FindPromotionResDto (
    val id : Long,
    val title : String,
    val content : String?,
    val imageUrl : String?,
    val rewardAmount : Long
)
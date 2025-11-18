package com.jinjindole2.jinpro.promotion.application.service.web

import com.jinjindole2.jinpro.promotion.application.port.`in`.web.FindJoinablePromotionUseCase
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionMongoPort
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionResDto
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.PaginatedRes
import com.jinjindole2.jinpro.common.exception.InvalidInputException
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class FindJoinablePromotion (
    private val viewPort: PromotionMongoPort
) : FindJoinablePromotionUseCase {

    override suspend fun findJoinablePromotion(userId: Long, page: Long, size: Long): PaginatedRes<FindPromotionResDto> {
        if (size > 10) throw InvalidInputException("최대 10개의 프로모션를 조회할 수 있습니다.")
        //TODO !! 예외처리
        return viewPort.countJoinable(userId)!!.let { totalCount ->
            if (totalCount == 0L) PaginatedRes(
                totalPages = 0L,
                totalCount = 0L,
                currentPage = 0L,
                pageSize = size,
                list = emptyList()
            ) else PaginatedRes(
                totalPages = ceil((totalCount / size).toDouble()).toLong(),
                totalCount = totalCount,
                currentPage = page,
                pageSize = size,
                list = if (page * size > totalCount) emptyList() else viewPort.findJoinableList(userId, size, page * size)
            )
        }
    }
}
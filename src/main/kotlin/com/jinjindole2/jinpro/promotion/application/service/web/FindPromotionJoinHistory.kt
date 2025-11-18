package com.jinjindole2.jinpro.promotion.application.service.web

import com.jinjindole2.jinpro.promotion.application.port.`in`.web.FindPromotionJoinHistoryUseCase
import com.jinjindole2.jinpro.promotion.application.port.out.PromotionMongoPort
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.FindPromotionJoinHistoryResDto
import com.jinjindole2.jinpro.promotion.adapter.`in`.web.dto.PaginatedRes
import com.jinjindole2.jinpro.common.exception.InvalidInputException
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class FindPromotionJoinHistory (
    private val viewPort: PromotionMongoPort
) : FindPromotionJoinHistoryUseCase {

    override suspend fun findPromotionJoinHistory(userId: Long, page: Long, size: Long): PaginatedRes<FindPromotionJoinHistoryResDto> {
        if (size > 50) throw InvalidInputException("참여 이력은 최대 50건 조회할 수 있습니다.")
        //TODO !! 예외처리
        return viewPort.countJoined(userId)!!.let { joinedCount ->
            if (joinedCount == 0L) PaginatedRes(
                totalPages = 0L,
                totalCount = 0L,
                currentPage = 0L,
                pageSize = size,
                list = emptyList()
            ) else PaginatedRes(
                totalPages = ceil((joinedCount / size).toDouble()).toLong(),
                totalCount = joinedCount,
                currentPage = page,
                pageSize = size,
                list = if (page * size > joinedCount) emptyList() else viewPort.findJoinedList(userId, size, page * size)
            )
        }
    }
}
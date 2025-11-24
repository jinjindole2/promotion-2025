package com.jinjindole2.dailypromotion.promotion.adapter.`in`.web

import com.jinjindole2.dailypromotion.common.exception.*
import com.jinjindole2.dailypromotion.common.response.ApiResponse
import com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.dto.*
import com.jinjindole2.dailypromotion.promotion.application.port.`in`.web.FindJoinablePromotionUseCase
import com.jinjindole2.dailypromotion.promotion.application.port.`in`.web.FindPromotionJoinHistoryUseCase
import com.jinjindole2.dailypromotion.promotion.application.port.`in`.web.JoinPromotionUseCase
import com.jinjindole2.dailypromotion.promotion.application.port.`in`.web.RegisterPromotionUseCase
import com.jinjindole2.dailypromotion.promotion.adapter.`in`.web.mapper.PromotionWebMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.LocalDate

@RestController
@RequestMapping("/promotion")
class PromotionController (
    private val findJoinablePromotionUseCase: FindJoinablePromotionUseCase,
    private val findPromotionJoinHistoryUseCase: FindPromotionJoinHistoryUseCase,
    private val joinPromotionUseCase: JoinPromotionUseCase,
    private val registerPromotionUseCase: RegisterPromotionUseCase,
    private val webMapper: PromotionWebMapper
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    // 1. 프로모션 등록 API
    @Operation(summary = "프로모션 등록 API", description = "시스템에 프로모션를 등록합니다.")
    @PostMapping("/")
    suspend fun regist(request: ServerHttpRequest,
                       @Valid @RequestBody @Parameter(description = "등록할 프로모션 정보") req: RegisterPromotionReqDto
    ) : ResponseEntity<Any> {
        try {
            // TODO 유효성 검사 : 완료일자가 시작일자보다 먼저면 안됨
            val registeredPromotion = registerPromotionUseCase.registerPromotion(webMapper.registReqToEntity(req))
            val locationUri = URI.create("${request.uri}${registeredPromotion.id}")
            return ResponseEntity.created(locationUri).build()
        } catch (e: DuplicatedTitleException) {
            throw CommonPromotionHttpException(ErrorCodes.DUPLICATED_TITLE, HttpStatus.CONFLICT)
        } catch (e: InvalidInputException) {
            throw CommonPromotionHttpException(ErrorCodes.INVALID_INPUT, HttpStatus.BAD_REQUEST, e.message.toString())
        }
    }

    // 1-1. 프로모션 참여 제한타입
    @PostMapping("/join-type")
    suspend fun addJoinType(request: ServerHttpRequest,
                            @Valid @RequestBody req: AddPromotionJoinTypeReqDto
    ) : ResponseEntity<Any> {
        try {
            // TODO 유효성 검사 : 선행, 후행이 같으면 안됨
            registerPromotionUseCase.addJoinType(webMapper.addJoinTypeReqToEntity(req))
            val locationUri = URI.create("${request.uri}/../${req.promoId}")
            return ResponseEntity.created(locationUri).build()
        } catch (e: PromotionNotFoundException) {
            throw CommonPromotionHttpException(ErrorCodes.PROMOTION_NOT_FOUND, HttpStatus.NOT_FOUND)
        } catch (e: LeadingPromotionNotFoundException) {
            throw CommonPromotionHttpException(ErrorCodes.LEADING_PROMOTION_NOT_FOUND, HttpStatus.NOT_FOUND)
        } catch (e: DuplicatedJoinTypeException) {
            throw CommonPromotionHttpException(ErrorCodes.DUPLICATED_JOIN_TYPE, HttpStatus.CONFLICT)
        } catch (e: DuplicatedLeadingPromotionException) {
            throw CommonPromotionHttpException(ErrorCodes.DUPLICATED_LEADING_PROMOTION, HttpStatus.CONFLICT)
        }
    }

    // 2. 고객 참여 가능한 프로모션 조회 API
    @GetMapping("/user/{userId}/available")
    suspend fun findAvailableList(@PathVariable userId: Long,
                                  @RequestParam(defaultValue = "0") page: Long,
                                  @RequestParam(defaultValue = "10") size: Long) : ApiResponse<PaginatedRes<FindPromotionResDto>> {
        val result = findJoinablePromotionUseCase.findJoinablePromotion(userId, page, size)
        return ApiResponse.just(result)
    }

    // 3. 고객 프로모션 참여 API
    @PostMapping("/join")
    suspend fun join(request: ServerHttpRequest,
                     @Valid @RequestBody req: JoinPromotionReqDto
    ) : ResponseEntity<Any> {
        try {
            joinPromotionUseCase.joinPromotion(req.promoId, req.userId, LocalDate.now())
            val locationUri = URI.create("${request.uri}/../${req.promoId}/user/${req.userId}/join")
            return ResponseEntity.created(locationUri).build()
        } catch (e: PromotionNotFoundException) {
            throw CommonPromotionHttpException(ErrorCodes.PROMOTION_NOT_FOUND, HttpStatus.NOT_FOUND)
        } catch (e: NotJoinableException) {
            throw CommonPromotionHttpException(ErrorCodes.NOT_JOINABLE, HttpStatus.UNPROCESSABLE_ENTITY)
        } catch (e: AlreadyJoinedException) {
            throw CommonPromotionHttpException(ErrorCodes.ALREADY_JOINED, HttpStatus.CONFLICT)
        }
    }

    // 4. 고객 프로모션 참여 이력 조회 API
    // TODO
    @GetMapping("/user/{userId}/join")
    suspend fun findJoinHistory(@PathVariable userId: Long,
                               @RequestParam(defaultValue = "0") @PositiveOrZero(message = "페이지는 0 부터 조회 가능합니다.")
                               page: Long,
                               @RequestParam(defaultValue = "50") @Positive(message = "페이지는 ???0 부터 조회 가능합니다.")
                               @Max(50, message = "한번에 최대 50개의 이력을 조회 가능합니다.")
                               size: Long) : ApiResponse<PaginatedRes<FindPromotionJoinHistoryResDto>> {
        val result = findPromotionJoinHistoryUseCase.findPromotionJoinHistory(userId, page, size)
        return ApiResponse.just(result)
    }
}
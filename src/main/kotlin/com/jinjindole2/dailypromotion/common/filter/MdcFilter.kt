package com.jinjindole2.dailypromotion.common.filter

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.util.*


@Component
@Order(1)
class MdcFilter: WebFilter {

    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val uuid = exchange.request.headers["x-txid"]?.getOrNull(0) ?: "${UUID.randomUUID()}".replace("-","")

        MDC.put("txid", uuid)
        logger.info( exchange.request.let { request ->
            "uri: [${request.method}] ${request.path}, ip: ${request.remoteAddress}${
                if(request.queryParams.isEmpty()) "" else ", query-param: ${request.queryParams}"
            }"
        })

        // ServerHttpRequest를 복사하여 새로운 값 추가
        val modifiedRequest = exchange.request.mutate()
            .header("x-txid", uuid)  // 추가 헤더로 requestId 전달
            .build()

        // 새로운 ServerHttpRequest를 담은 ServerWebExchange로 교체
        val modifiedExchange = exchange.mutate().request(modifiedRequest).build()

        return chain.filter(modifiedExchange).contextWrite {
            Context.of("txid", uuid)
        }
    }
}
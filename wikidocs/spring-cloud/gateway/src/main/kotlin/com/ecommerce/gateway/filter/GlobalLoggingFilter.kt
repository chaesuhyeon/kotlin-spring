package com.ecommerce.gateway.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * 글로벌 필터
 * 모든 라우트에 공통 로직 적용
 */
@Component
class GlobalLoggingFilter : GlobalFilter, Ordered {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request

        // --- Pre-Filter: 요청이 마이크로서비스로 가기 전 ---
        log.info(">>>>> [Global Filter] Request: ID={}, URI={}, Path={}", request.id, request.uri, request.path)

        // --- Post-Filter: 마이크로서비스에서 응답이 돌아온 후 ---
        // chain.filter(exchange)가 실행되어야 다음 필터 또는 목적지로 요청이 전달
        // .then() 블록은 모든 로직이 끝난 후 실행
        return chain.filter(exchange).then(Mono.fromRunnable {
            val response = exchange.response
            log.info("<<<<< [Global Filter] Response: Status={}", response.statusCode)
        })
    }

    // 필터의 실행 순서를 지정. 낮은 값일수록 먼저 실행
    override fun getOrder(): Int {
        return 0
    }
}
package com.ecommerce.gateway.filter

import com.ecommerce.gateway.util.JwtUtil
import io.jsonwebtoken.JwtException
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthenticationFilter(
    private val jwtUtil: JwtUtil
) : AbstractGatewayFilterFactory<AuthenticationFilter.Config>(Config::class.java) {

    // 설정 클래스 (현재는 사용하지 않음)
    class Config

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request

            // 1. 공개된 엔드포인트는 필터링하지 않음 (예: /join, /login)
            if (request.uri.path.contains("/join") || request.uri.path.contains("/login")) {
                return@GatewayFilter chain.filter(exchange)
            }

            // 2. 'Authorization' 헤더 존재 여부 확인
            if (!request.headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return@GatewayFilter onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED)
            }

            // 3. 'Bearer ' 접두사로 시작하는 토큰 추출
            val authorizationHeader = request.headers[HttpHeaders.AUTHORIZATION]!![0]
            if (!authorizationHeader.startsWith("Bearer ")) {
                return@GatewayFilter onError(exchange, "Invalid token format", HttpStatus.UNAUTHORIZED)
            }
            val token = authorizationHeader.substring(7)

            // 4. 토큰 유효성 검증
            try {
                // (만료 여부 등 상세 검증은 JwtUtil 내부에 구현 가능)
                val memberId = jwtUtil.getMemberId(token)

                // 5. 검증 성공 시, 요청에 'X-Member-Id' 헤더 추가
                val modifiedRequest = request.mutate()
                    .header("X-Member-Id", memberId.toString())
                    .build()

                chain.filter(exchange.mutate().request(modifiedRequest).build())
            } catch (e: JwtException) {
                onError(exchange, "Invalid token: ${e.message}", HttpStatus.UNAUTHORIZED)
            }
        }
    }

    private fun onError(exchange: ServerWebExchange, err: String, httpStatus: HttpStatus): Mono<Void> {
        val response = exchange.response
        response.statusCode = httpStatus
        // (로그 남기는 로직 추가)
        return response.setComplete()
    }
}
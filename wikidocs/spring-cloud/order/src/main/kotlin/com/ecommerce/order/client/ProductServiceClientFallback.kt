package com.ecommerce.order.client

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Fallback이란?
 * 주 로직(API 호출)이 실패했을 떄, 실행되는 대체 로직을 의미한다.
 * 서킷 브레이커가 OPEN 상태가 되어 CallNotPermittedException을 던지는 대신, 미리 정의된 '대체 응답'이나 '대체 동작'을 수행하는 것이다.
 */
@Component
class ProductServiceClientFallback :ProductServiceClient {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 이 메서드는 ProductServiceClient.getProduct() 호출이 실패하면
     * (서킷 OPEN, 타임아웃 등) 대신 호출된다.
     */
    override fun getProduct(productId: Long): ProductResponse {
        log.warn("Fallback for getProduct(productId={}) triggered.", productId)
        // 여기에 대체 로직을 구현한다.

        // 1. 미리 정의된 기본(Default) 응답 반환
        return ProductResponse(
            id = productId,
            name = "상품 정보 조회 불가", // 사용자에게 표시될 메시지
            price = -1L, // 에러를 나타내는 값
            stockQuantity = 0
        )

        // 2. (더 발전된 방식) Redis 같은 캐시에서 오래된(Stale) 데이터라도 조회하여 반환
        // val cachedProduct = redisCache.get("product:$productId")
        // return cachedProduct ?: defaultResponse
    }
}
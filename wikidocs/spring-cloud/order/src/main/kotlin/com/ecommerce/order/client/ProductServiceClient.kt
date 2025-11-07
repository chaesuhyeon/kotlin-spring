package com.ecommerce.order.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "product-service", // name: 호출할 서비서의 Eureka/Consule 등록 이름(서비스ID)
    fallback = ProductServiceClientFallback::class) // Fallback 클래스 등록
interface ProductServiceClient {

    // 호출할 product-service의 API 시그니처와 동일하게 메서드를 선언
    @GetMapping("/api/v1/products/{productId}")
    fun getProduct(@PathVariable productId: Long): ProductResponse

    // (예시) 상품 재고 차감을 위한 API 호출
    // @PostMapping("/api/v1/products/decrease-stock")
    // fun decreaseStock(@RequestBody request: DecreaseStockRequest)
}
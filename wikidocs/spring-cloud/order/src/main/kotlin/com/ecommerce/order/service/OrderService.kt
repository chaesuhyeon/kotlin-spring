package com.ecommerce.order.service

import com.ecommerce.order.client.CreateOrderRequest
import com.ecommerce.order.client.ProductServiceClient
import com.ecommerce.order.domain.OrderRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class OrderService (
    private val orderRepository: OrderRepository,
    private val productServiceClient: ProductServiceClient
    ) {

    @Transactional
    fun createOrder(memberId: Long, request: CreateOrderRequest) {

        // 간결하게 API 호출
        val product = productServiceClient.getProduct(request.productId)

        // 재고 확인 로직
        if (product.stockQuantity < request.quantity) {
            throw IllegalArgumentException("재고가 부족합니다.")
        }

        // ... (주문 생성 로직) ...
    }

}
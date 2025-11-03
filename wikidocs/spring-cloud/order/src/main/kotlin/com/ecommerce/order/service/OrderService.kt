package com.ecommerce.order.service

import com.ecommerce.order.client.CreateOrderRequest
import com.ecommerce.order.client.ProductServiceClient
import com.ecommerce.order.domain.Order
import com.ecommerce.order.domain.OrderRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class OrderService (
    private val orderRepository: OrderRepository,
    private val productServiceClient: ProductServiceClient
    ) {

    @Transactional
    fun createOrder(memberId: Long, productId: Long, quantity: Int) : Order {

        // 간결하게 API 호출
        val product = productServiceClient.getProduct(productId)

        // 재고 확인 로직
        if (product.stockQuantity < quantity) {
            throw IllegalArgumentException("재고가 부족합니다.")
        }

        val order = Order(
            memberId = memberId,
            productId = product.id,
            quantity = quantity,
            unitPrice = product.price // API로 조회한 실시간 가격 사용
        )

        return orderRepository.save(order)
    }

}
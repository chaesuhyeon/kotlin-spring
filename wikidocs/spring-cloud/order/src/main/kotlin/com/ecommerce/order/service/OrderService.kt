package com.ecommerce.order.service

import com.ecommerce.order.api.OrderResponse
import com.ecommerce.order.api.toResponse
import com.ecommerce.order.client.CreateOrderRequest
import com.ecommerce.order.client.ProductServiceClient
import com.ecommerce.order.domain.Order
import com.ecommerce.order.domain.OrderLineItem
import com.ecommerce.order.domain.OrderRepository
import com.ecommerce.order.event.OrderPaidEvent
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.util.LinkedList

@Service
class OrderService (
    private val orderRepository: OrderRepository,
    private val productServiceClient: ProductServiceClient,
    private val streamBridge: StreamBridge
    ) {
    private val log = LoggerFactory.getLogger(javaClass)

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
            unitPrice = product.price, // API로 조회한 실시간 가격 사용
            totalAmount = quantity * product.price,
            orderLines = LinkedList<OrderLineItem>()
        )

        return orderRepository.save(order)
    }

    @Transactional
    fun createAndPayOrder(memberId: Long, request: CreateOrderRequest): OrderResponse {
        // 간결하게 API 호출
        val product = productServiceClient.getProduct(request.productId)

        // 재고 확인 로직
        if (product.stockQuantity < request.quantity) {
            throw IllegalArgumentException("재고가 부족합니다.")
        }

        val order = Order(
            memberId = memberId,
            productId = product.id,
            quantity = request.quantity,
            unitPrice = product.price, // API로 조회한 실시간 가격 사용
            totalAmount = request.quantity * product.price,
            orderLines = LinkedList<OrderLineItem>()
        )

        val savedOrder = orderRepository.save(order)

        // (결제 서비스 연동 로직)
        // paymentService.processPayment(...)
        // 결제가 성공했다고 가정
//        savedOrder.markAsPaid()

        // 이벤트 생성
        val event = OrderPaidEvent(
            orderId = savedOrder.id!!,
            memberId = savedOrder.memberId,
            totalAmount = savedOrder.totalAmount.toBigDecimal(),
            orderLines = savedOrder.orderLines.map {
                OrderPaidEvent.OrderLineItem(it.productId, it.quantity)
            }
        )

        // --- StreamBridge로 이벤트 발행 ---
        // 첫 번째 인자: application.yml에 정의한 바인딩 이름
        // 두 번째 인자: 보낼 이벤트 객체
        val isSent = streamBridge.send("orderPaidEventProducer-out-0", event)
        log.info("OrderPaidEvent sent for orderId {}: {}", savedOrder.id, isSent)

        return savedOrder.toResponse()
    }
}
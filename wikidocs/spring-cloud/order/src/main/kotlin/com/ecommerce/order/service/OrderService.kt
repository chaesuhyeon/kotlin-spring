package com.ecommerce.order.service

import com.ecommerce.order.api.OrderResponse
import com.ecommerce.order.api.toResponse
import com.ecommerce.order.client.CreateOrderRequest
import com.ecommerce.order.client.ProductServiceClient
import com.ecommerce.order.command.CancelOrderCommand
import com.ecommerce.order.command.CreateOrderCommand
import com.ecommerce.order.command.UpdateShippingInfoCommand
import com.ecommerce.order.domain.Order
import com.ecommerce.order.domain.OrderLineItem
import com.ecommerce.order.domain.OrderRepository
import com.ecommerce.order.event.OrderPaidEvent
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.data.repository.findByIdOrNull
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
    fun createOrder(command : CreateOrderCommand) : Order {

        // 간결하게 API 호출
        val product = productServiceClient.getProduct(command.productId)

        // 재고 확인 로직
        if (product.stockQuantity < command.quantity) {
            throw IllegalArgumentException("재고가 부족합니다.")
        }

        val order = Order(
            memberId = command.memberId,
            productId = product.id,
            quantity = command.quantity,
            unitPrice = product.price, // API로 조회한 실시간 가격 사용
            totalAmount = command.quantity * product.price,
            orderLines = LinkedList<OrderLineItem>(),
            shippingAddress = command.shippingAddress
        )

        return orderRepository.save(order)
    }

    @Transactional
    fun createAndPayOrder(command : CreateOrderCommand): OrderResponse {
        // 간결하게 API 호출
        val product = productServiceClient.getProduct(command.productId)

        // 재고 확인 로직
        if (product.stockQuantity < command.quantity) {
            throw IllegalArgumentException("재고가 부족합니다.")
        }

        val order = Order(
            memberId = command.memberId,
            productId = product.id,
            quantity = command.quantity,
            unitPrice = product.price, // API로 조회한 실시간 가격 사용
            totalAmount = command.quantity * product.price,
            orderLines = LinkedList<OrderLineItem>(),
            shippingAddress = command.shippingAddress
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

    /**
     * 배송지 변경 Command 처리
     */
    fun updateShippingInfo(command: UpdateShippingInfoCommand) {
        // 1. 애그리거트 루트를 조회
        val order = orderRepository.findByIdOrNull(command.orderId)
            ?: throw EntityNotFoundException("주문을 찾을 수 없습니다.")

        // 2. 애그리거트의 비즈니스 메서드 호출하여 상태 변경 위임
        order.updateShippingInfo(command.newShippingAddress) // 더티 체킹
    }

    /**
     * 주문 취소 Command 처리
     */
    fun cancelOrder(command: CancelOrderCommand) {
        val order = orderRepository.findByIdOrNull(command.orderId)
            ?: throw EntityNotFoundException("주문을 찾을 수 없습니다.")

        order.cancel()
    }
}
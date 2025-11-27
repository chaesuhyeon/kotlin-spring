package com.ecommerce.order.service

import com.ecommerce.order.client.ProductServiceClient
import com.ecommerce.order.command.CancelOrderCommand
import com.ecommerce.order.command.CreateOrderCommand
import com.ecommerce.order.command.UpdateShippingInfoCommand
import com.ecommerce.order.domain.Order
import com.ecommerce.order.domain.OrderLineItem
import com.ecommerce.order.domain.OrderRepository
import com.ecommerce.order.event.OrderCreatedEvent
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.LinkedList

@Service
class OrderService (
    private val orderRepository: OrderRepository,
    private val productServiceClient: ProductServiceClient,
    private val orderEventProducer: OrderEventProducer
    ) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun createOrder(command: CreateOrderCommand): Long {
        // 간결하게 API 호출
        val product = productServiceClient.getProduct(command.productId)

        // 주문을 'PENDING' 상태로 생성하고 DB에 저장
        val newOrder = Order(
            memberId = command.memberId,
            productId = command.productId,
            quantity = command.quantity,
            unitPrice = product.price, // API로 조회한 실시간 가격 사용
            totalAmount = command.quantity * product.price,
            orderLines = LinkedList<OrderLineItem>(),
            shippingAddress = command.shippingAddress,
            orderedAt = LocalDateTime.now(),

        )
        val savedOrder = orderRepository.save(newOrder)

        // (결제 서비스 연동 로직)
        // paymentService.processPayment(...)
        // 결제가 성공했다고 가정
//        savedOrder.markAsPaid()


        // SAGA를 시작하기 위한 첫 번째 이벤트 생성
        val event = OrderCreatedEvent(
            orderId = savedOrder.id!!,
            productId = savedOrder.productId,
            quantity = savedOrder.quantity,
            memberId = savedOrder.memberId,
            totalAmount = savedOrder.totalAmount,
            orderedAt = savedOrder.orderedAt
        )

        // Kafka로 이벤트 발행
        orderEventProducer.sendOrderCreatedEvent(event)

        return savedOrder.id!!
    }


    @Transactional
    fun confirmOrder(orderId: Long) {
        val order = orderRepository.findByIdOrNull(orderId) ?: return
        order.confirm() // 주문 상태를 CONFIRMED로 변경
        orderRepository.save(order)
        log.info("Order for orderId {} has been confirmed.", orderId)
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

    /**
     * 주문 취소 SAGA 패턴 적용 로직
     */
    @Transactional
    fun cancelOrderForSaga(orderId: Long, reason: String) {
        val order = orderRepository.findByIdOrNull(orderId)
            ?: throw EntityNotFoundException("주문을 찾을 수 없습니다.")

        // 멱등성 체크 및 비즈니스 로직은 Order 애그리거트가 담당
        order.cancel()
        orderRepository.save(order)
    }
}
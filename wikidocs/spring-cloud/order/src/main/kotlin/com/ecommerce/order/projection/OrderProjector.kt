package com.ecommerce.order.projection

import com.ecommerce.order.client.ProductServiceClient
import com.ecommerce.order.domain.OrderStatus
import com.ecommerce.order.event.OrderCancelledEvent
import com.ecommerce.order.event.OrderCreatedEvent
import com.ecommerce.order.query.OrderSummary
import com.ecommerce.order.query.OrderSummaryRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * command 측 변경 내용을 query 측에 반영하기 위해서는 이벤트 기반의 데이터 동기화 로직이 필요하다.(Event-Driven Synchronization)
 *  orderService에 order를 create하고(command) orderSummaries(query) 테이블에도 save하는 방식은 절대 사용하면 안되는 안티패턴이다.
 *  '쓰기'를 책임지는 OrderService가 '읽기'모델의 존재와 구조를 알아야하기 때문에 '단일 책임 원칙 위배'의 문제가 발생한다.
 *  OrderSummaryRepository의 저장이 실패한다면 orderRepository.save(order)까지 롤백되어야할까? 와 같은 '트랜잭션' 문제가 발생할 수 있다.
 *  '쓰기' 작업이 '읽기' 모델 테이블까지 lock을 잡게되어 cqrs의 목적인 성능 향상에 위배된다.
 * OrderProjector는 '이벤트를 받아 읽기 모델을 만드는 책임'만 지는 컴포넌트이다.
 */
@Component
class OrderProjector (
    private val orderSummaryRepository : OrderSummaryRepository,
    private val productServiceClient: ProductServiceClient // 읽기 모델에 필요한 추가 정보 조달
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["ecommerce.orders.created"], groupId = "order-projection-group")
    fun onOrderCreated(event: OrderCreatedEvent) {
        log.info("Projecting OrderCreatedEvent: {}", event)
        try {
            // 읽기 모델에 '상품명'이 필요하므로, Feign 클라이언트로 상품 정보 조회
            val product = productServiceClient.getProduct(event.productId)

            // 읽기 전용 DTO/엔티티인 OrderSummary 생성
            val summary = OrderSummary(
                orderId = event.orderId,
                memberId = event.memberId,
                productName = product.name, // 비정규화된 데이터 저장
                totalAmount = event.totalAmount,
                status = OrderStatus.PENDING,
                orderedAt = event.orderedAt
            )
            orderSummaryRepository.save(summary)

        } catch (e: Exception) {
            log.error("Error projecting OrderCreatedEvent for orderId ${event.orderId}", e)
            // (에러 처리 및 재시도 로직)
        }
    }

    @KafkaListener(topics = ["ecommerce.orders.cancelled"], groupId = "order-projection-group")
    fun onOrderCancelled(event: OrderCancelledEvent) {
        log.info("Projecting OrderCancelledEvent: {}", event)
        // 기존 읽기 모델을 찾아 상태만 변경
        orderSummaryRepository.findById(event.orderId).ifPresent { summary ->
            summary.status = OrderStatus.CANCELLED
            orderSummaryRepository.save(summary)
        }
    }
}
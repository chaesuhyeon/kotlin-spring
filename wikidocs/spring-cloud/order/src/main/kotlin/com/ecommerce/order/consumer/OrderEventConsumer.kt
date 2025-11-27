package com.ecommerce.order.consumer

import com.ecommerce.order.event.StockDecreaseFailedEvent
import com.ecommerce.order.event.StockDecreasedEvent
import com.ecommerce.order.service.OrderService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * [SAGA 패턴 적용]
 * StockDecreaseFailedEvent를 구독하여, SAGA의 시작점이었던 '주문'의 상태를 CANCELLED로 변경하는 보상 트랜잭션을 실행
 */
@Component
class OrderEventConsumer (
    private val orderService: OrderService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 재고 차감 실패 이벤트를 구독하여 주문 취소 보상 트랜잭션을 실행
     */
    @KafkaListener(topics = ["ecommerce.stock.decrease.failed"], groupId = "order-compensation-group")
    fun handleStockDecreaseFailed(event: StockDecreaseFailedEvent) {
        log.info("StockDecreaseFailedEvent received. Cancelling order for orderId: {}", event.orderId)
        orderService.cancelOrderForSaga(event.orderId, event.reason)
    }


    /**
     * 재고 차감 이벤트를 구독하여 SAGA의 최종 상태를 반영
     */
    @KafkaListener(topics = ["ecommerce.stock.decreased"], groupId = "order-group")
    fun handleStockDecreased(event: StockDecreasedEvent) {
        log.info("StockDecreasedEvent received. Confirming order for orderId: {}", event.orderId)
        orderService.confirmOrder(event.orderId)
    }
}
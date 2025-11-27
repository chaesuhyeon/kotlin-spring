package com.ecommerce.product.consumer

import com.ecommerce.product.event.PaymentCompletedEvent
import com.ecommerce.product.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * 1. order-service가 주문에 성공하면 'OrderCreatedEvent'를 발행한다.
 * 2. payment-service가 'OrderCreatedEvent'를 구독하여 결제를 처리하고, SAGA의 다음 단계를 위한 'PaymentCompletedEvent'를 발행한다.
 * 3. product-service는 'PaymentCompletedEvent'를 구독하여 재고를 차감하고 'StockDecreasedEvent'를 발행한다.
 * 4. order-service는 'StockDecreasedEvent', 'StockDecreaseFailEvent'를 모두 구독하여 SAGA의 최종 상태를 반영한다.
 */
@Component
class ProductEventConsumer (
    private val productService: ProductService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["ecommerce.payments.completed"], groupId = "product-group")
    fun handlePaymentCompleted(event: PaymentCompletedEvent) {
        log.info("PaymentCompletedEvent received. Decreasing stock for orderId: {}", event.orderId)
        productService.decreaseStockForSaga(event)
    }
}
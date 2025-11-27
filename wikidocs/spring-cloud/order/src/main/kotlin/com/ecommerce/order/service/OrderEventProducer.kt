package com.ecommerce.order.service

import com.ecommerce.order.event.OrderCreatedEvent
import com.ecommerce.order.event.OrderPaidEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

/**
 * OrderService는 이제 StreamBridge 대신 이 OrderEventProducer를 주입받아 이벤트를 발행하면 된다.
 */
@Service
class OrderEventProducer(
    // KafkaTemplate 주입 (Key: String, Value: OrderPaidEvent)
    private val kafkaTemplate: KafkaTemplate<String, OrderPaidEvent>
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val topic = "ecommerce.orders.paid"

    fun sendOrderPaidEvent(event: OrderPaidEvent) {
        // '파티셔닝 키'를 orderId로 지정
        // -> 동일한 orderId를 가진 이벤트는 항상 동일한 파티션으로 전송됨
        val key = event.orderId.toString()

        log.info("Sending OrderPaidEvent with key={}: {}", key, event)
        // send(토픽, 키, 값)
        kafkaTemplate.send(topic, key, event)
    }

    fun sendOrderCreatedEvent(event: OrderCreatedEvent) {}
}

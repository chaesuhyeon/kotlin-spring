package com.ecommerce.shipping.consumer

import com.ecommerce.shipping.service.OrderPaidEvent
import com.ecommerce.shipping.service.ShippingService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ShippingEventConsumer(
    private val shippingService: ShippingService // 1. 비즈니스 로직을 처리할 서비스
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun onOrderPaid(): (OrderPaidEvent) -> Unit = { event ->
        log.info(">> Received OrderPaidEvent: {}", event)
        try {
            // 2. 서비스 레이어에 작업 위임
            shippingService.startShipping(event)
        } catch (e: Exception) {
            // 3. 에러 처리
            log.error("Failed to process shipping for orderId: ${event.orderId}", e)
            // 예외를 던지면 Spring Cloud Stream의 재시도/DLQ 메커니즘이 동작
            throw e
        }
    }
}
// ============ 실패 처리 ============
//만약 shippingService.startShipping(event) 로직 수행 중 shipping-service의 DB 장애로 예외가 발생하면 어떻게 될까?
//Spring Cloud Stream Kafka Binder는 기본적으로 매우 견고한 실패 처리 메커니즘을 제공한다.
//      재시도 (Retry): 메시지 처리에 실패하면, 기본적으로 3번 더 재시도한다. 일시적인 네트워크 문제라면 재시도 과정에서 성공할 수 있다.
//      데드 레터 큐 (Dead Letter Queue, DLQ): 재시도 횟수를 모두 소진해도 계속 실패할경우 별도의 DLQ 토픽으로 격리된다. (예: error.ecommerce.orders.paid.shipping-service-group)
//이는 '문제 있는 메시지' 하나 때문에 전체 파티션의 다른 정상적인 메시지 처리가 막히는 것을 방지하는 매우 중요한 기능이다. 개발자는 나중에 DLQ에 쌓인 메시지를 분석하여 원인을 파악하고 수동으로 복구할 수 있다.
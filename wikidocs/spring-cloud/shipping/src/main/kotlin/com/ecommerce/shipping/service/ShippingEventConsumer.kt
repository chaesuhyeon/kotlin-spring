package com.ecommerce.shipping.service

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ShippingEventConsumer {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * OrderPaidEvent를 소비하는 Consumer 함수를 Bean으로 등록
     * 이 함수는 Kafka에 대한 어떤 지식도 가지고 있지 않음
     */
    @Bean
    fun onOrderPaid(): (OrderPaidEvent) -> Unit = { event ->
        log.info("OrderPaidEvent received: {}", event)
        // TODO: 이 이벤트를 기반으로 배송 시작 로직 구현
    }
}

// (OrderPaidEvent data class는 공통 모듈 또는 각 서비스에 정의)
data class OrderPaidEvent(val orderId: Long, val memberId: Long)
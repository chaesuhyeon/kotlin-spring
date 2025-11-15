package com.ecommerce.shipping.service

import com.ecommerce.shipping.domain.Shipment
import com.ecommerce.shipping.domain.ShipmentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ShippingService(
    private val shipmentRepository: ShipmentRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun startShipping(event: OrderPaidEvent) {
        log.info("Starting shipping process for orderId: {}", event.orderId)

        // 1. 이미 처리된 이벤트인지 멱등성(Idempotency) 체크 (선택적이지만 중요)
        if (shipmentRepository.existsByOrderId(event.orderId)) {
            log.warn("Shipping process for orderId {} already started.", event.orderId)
            return
        }

        // 2. 이벤트 정보를 바탕으로 Shipment 엔티티 생성
        val shipment = Shipment(
            orderId = event.orderId,
            memberId = event.memberId,
            // ... 배송지 정보 등은 이벤트를 더 풍부하게 만들거나,
            // order-service에 API를 동기 호출하여 조회할 수도 있음
        )

        // 3. DB에 저장
        shipmentRepository.save(shipment)
        log.info("Shipment created for orderId: {}", event.orderId)
    }
}
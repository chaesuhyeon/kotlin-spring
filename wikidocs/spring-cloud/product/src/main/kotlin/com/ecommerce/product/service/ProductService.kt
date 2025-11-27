package com.ecommerce.product.service

import com.ecommerce.product.api.ProductResponse
import com.ecommerce.product.domain.ProductRepository
import com.ecommerce.product.event.PaymentCompletedEvent
import com.ecommerce.product.event.StockDecreaseFailedEvent
import com.ecommerce.product.event.StockDecreasedEvent
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService (
    private val productRepository: ProductRepository,
    private val productEventProducer: ProductEventProducer, // Kafka 프로듀서
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun getProduct(productId : Long) : ProductResponse {
        return ProductResponse(productId, "productName", 1000L, 10);
    }

    @Transactional
    fun decreaseStockForSaga(event: PaymentCompletedEvent) {
        try {
            val product = productRepository.findByIdOrNull(event.productId) ?: throw Exception("상품 없음")
            // 재고 차감 로직 (실패 가능성 있음)
            product.decreaseStock(event.quantity) // (내부에서 재고 부족 시 Exception 발생)
            productRepository.save(product)

            // 성공 시: StockDecreasedEvent 발행
            val successEvent = StockDecreasedEvent(orderId = event.orderId)
            productEventProducer.sendStockDecreasedEvent(successEvent)

        } catch (e: Exception) {
            log.warn("Failed to decrease stock for orderId: {}. Reason: {}", event.orderId, e.message)
            // 실패 시: StockDecreaseFailedEvent (보상 트랜잭션 트리거) 발행
            val failEvent = StockDecreaseFailedEvent(
                orderId = event.orderId,
                memberId = event.memberId,
                reason = e.message ?: "UNKNOWN_ERROR",
                orderLines = emptyList(),
            )
            productEventProducer.sendStockDecreaseFailedEvent(failEvent)
        }
    }
}
package com.ecommerce.order.event

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderCreatedEvent(
    val orderId: Long,
    val memberId: Long,
    val totalAmount: BigDecimal,
    val productId: Long,
    val orderedAt:LocalDateTime
) {

}
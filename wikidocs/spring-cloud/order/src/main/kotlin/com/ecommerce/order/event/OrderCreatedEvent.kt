package com.ecommerce.order.event

import java.time.LocalDateTime

data class OrderCreatedEvent(
    val orderId: Long,
    val memberId: Long,
    val quantity: Int,
    val totalAmount: Long,
    val productId: Long,
    val orderedAt:LocalDateTime
) {

}
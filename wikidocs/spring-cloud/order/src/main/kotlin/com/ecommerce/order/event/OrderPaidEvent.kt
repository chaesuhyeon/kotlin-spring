package com.ecommerce.order.event

import java.math.BigDecimal

data class OrderPaidEvent(
    val orderId: Long,
    val memberId: Long,
    val totalAmount: BigDecimal,
    val orderLines: List<OrderLineItem>,
) {
    data class OrderLineItem(
        val productId: Long,
        val quantity: Int,
    )
}
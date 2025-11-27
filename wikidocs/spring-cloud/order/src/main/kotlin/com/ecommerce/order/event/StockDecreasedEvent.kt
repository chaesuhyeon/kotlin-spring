package com.ecommerce.order.event

data class StockDecreasedEvent(
    val orderId: Long,
) {
    data class OrderLineItem(val productId: Long, val quantity: Int)
}
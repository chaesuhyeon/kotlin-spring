package com.ecommerce.order.event

data class StockDecreaseFailedEvent(
    val orderId: Long,
    val memberId: Long,
    val reason: String, // 실패 사유 (예: "OUT_OF_STOCK")
    val orderLines: List<OrderLineItem>, // (재고 복구를 위한 상품 정보)
) {
    data class OrderLineItem(val productId: Long, val quantity: Int)
}
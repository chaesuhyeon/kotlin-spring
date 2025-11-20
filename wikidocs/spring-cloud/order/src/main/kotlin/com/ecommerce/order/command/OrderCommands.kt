package com.ecommerce.order.command

// 주문 생성을 위한 Command
data class CreateOrderCommand(
    val memberId: Long,
    val productId: Long,
    val quantity: Int,
    val shippingAddress: String,
)

// 배송지 변경을 위한 Command
data class UpdateShippingInfoCommand(
    val orderId: Long,
    val newShippingAddress: String,
)

// 주문 취소를 위한 Command
data class CancelOrderCommand(
    val orderId: Long,
)
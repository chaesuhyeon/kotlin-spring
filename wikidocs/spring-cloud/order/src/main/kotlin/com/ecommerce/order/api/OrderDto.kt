package com.ecommerce.order.api

import com.ecommerce.order.domain.Order

data class OrderResponse(
    val id: Long?,
    val memberId: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Long,
    val totalAmount: Long
)


fun Order.toResponse(): OrderResponse {
    return OrderResponse(
        id = this.id,
        memberId = this.memberId,
        productId = this.productId,
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        totalAmount = this.totalAmount
    )
}

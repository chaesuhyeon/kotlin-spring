package com.ecommerce.product.event

class PaymentCompletedEvent (
    val orderId: Long,
    val memberId: Long,
    val productId: Long,
    val quantity: Int,
) {}
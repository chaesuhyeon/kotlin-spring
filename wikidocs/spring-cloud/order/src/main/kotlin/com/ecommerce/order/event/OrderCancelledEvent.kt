package com.ecommerce.order.event

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderCancelledEvent(
    val orderId: Long,
    val memberId: Long,
) {}
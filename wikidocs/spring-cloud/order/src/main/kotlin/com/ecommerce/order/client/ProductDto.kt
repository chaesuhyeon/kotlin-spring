package com.ecommerce.order.client


// product-service의 응답 DTO와 동일한 구조
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val stockQuantity: Int,
)

data class CreateOrderRequest(
    val productId: Long,
    val quantity: Int
)
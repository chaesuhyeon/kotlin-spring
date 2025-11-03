package com.ecommerce.product.api

// 상품 정보 응답 DTO
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val stockQuantity: Int,
)
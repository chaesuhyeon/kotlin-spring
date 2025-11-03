package com.ecommerce.product.service

import com.ecommerce.product.api.ProductResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService {
    @Transactional
    fun getProduct(productId : Long) : ProductResponse {
        return ProductResponse(productId, "productName", 1000L, 10);
    }
}
package com.ecommerce.product.api

import com.ecommerce.product.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {
    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: Long): ProductResponse {
        return productService.getProduct(productId)
    }
}
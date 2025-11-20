package com.ecommerce.order.api

import com.ecommerce.order.client.CreateOrderRequest
import com.ecommerce.order.client.UpdateShippingRequest
import com.ecommerce.order.command.CancelOrderCommand
import com.ecommerce.order.command.CreateOrderCommand
import com.ecommerce.order.command.UpdateShippingInfoCommand
import com.ecommerce.order.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<Void> {
        val command = CreateOrderCommand(
            memberId = 1L, // (인증 헤더에서 가져와야 함)
            productId = request.productId,
            quantity = request.quantity,
            shippingAddress = request.shippingAddress
        )

        val orderId = orderService.createOrder(command)
        // 생성된 리소스의 위치를 Location 헤더에 담아 201 Created 응답
        return ResponseEntity.created(URI.create("/api/v1/orders/$orderId")).build()
    }

    @PatchMapping("/{orderId}/shipping-info")
    fun updateShippingInfo(
        @PathVariable orderId: Long,
        @RequestBody request: UpdateShippingRequest
    ): ResponseEntity<Void> {
        val command = UpdateShippingInfoCommand(
            orderId = orderId,
            newShippingAddress = request.newAddress
        )
        orderService.updateShippingInfo(command)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: Long): ResponseEntity<Void> {
        val command = CancelOrderCommand(orderId)
        orderService.cancelOrder(command)
        return ResponseEntity.ok().build()
    }
}
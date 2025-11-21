package com.ecommerce.order.api

import com.ecommerce.order.query.OrderSummary
import com.ecommerce.order.service.OrderQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderQueryController (
    private val orderQueryService: OrderQueryService
){
    @GetMapping
    fun getMyOrders(@RequestHeader("X-Member-Id") memberId: Long): List<OrderSummary> {
        // '쓰기' 로직과 분리된, '읽기' 전용 서비스를 호출한다.
        return orderQueryService.getOrderSummaries(memberId)
    }
}
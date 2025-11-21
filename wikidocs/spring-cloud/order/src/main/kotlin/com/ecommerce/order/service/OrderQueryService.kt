package com.ecommerce.order.service

import com.ecommerce.order.query.OrderSummary
import com.ecommerce.order.query.OrderSummaryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderQueryService (
    private val orderSummaryRepository : OrderSummaryRepository
) {
    fun getOrderSummaries(memberId: Long): List<OrderSummary> {
        // 읽기 전용 DB만 조회하므로 매우 빠름
        return orderSummaryRepository.findByMemberIdOrderByOrderedAtDesc(memberId)
    }
}
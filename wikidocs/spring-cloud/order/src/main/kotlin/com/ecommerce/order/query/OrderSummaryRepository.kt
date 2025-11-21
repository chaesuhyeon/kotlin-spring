package com.ecommerce.order.query

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderSummaryRepository : JpaRepository<OrderSummary, Long> {
    // 특정 회원의 주문 목록을 최신순으로 조회
    fun findByMemberIdOrderByOrderedAtDesc(memberId: Long): List<OrderSummary>
}
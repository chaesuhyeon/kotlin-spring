package com.ecommerce.order.domain

import com.ecommerce.order.event.OrderPaidEvent
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
class OrderLineItem (
    val productId: Long,
    val quantity: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null // id는 DB가 생성하므로 주 생성자에서 제외
}
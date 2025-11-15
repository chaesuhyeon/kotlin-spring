package com.ecommerce.shipping.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Shipment (
    val memberId: Long,
    val orderId: Long
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null // id는 DB가 생성하므로 주 생성자에서 제외
}
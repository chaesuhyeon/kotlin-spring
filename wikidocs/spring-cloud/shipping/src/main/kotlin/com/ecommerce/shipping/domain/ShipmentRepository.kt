package com.ecommerce.shipping.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ShipmentRepository : JpaRepository<Shipment, Long>{
    fun existsByOrderId(orderId: Long): Boolean
}
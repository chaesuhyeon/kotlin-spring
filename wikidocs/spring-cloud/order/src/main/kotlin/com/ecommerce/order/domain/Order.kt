package com.ecommerce.order.domain

import com.ecommerce.order.event.OrderPaidEvent
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order (
    val memberId: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Long,
    val totalAmount:Long,
    val orderLines: List<OrderLineItem>,
    var shippingAddress: String,
    var status: OrderStatus = OrderStatus.PENDING,
    val orderedAt : LocalDateTime
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null // id는 DB가 생성하므로 주 생성자에서 제외

    // Order 애그리거트가 Command를 처리할 비즈니스 로직을 갖는다.
    // 애그리거트 루트는 외부의 데이터를 받아 스스로의 상태를 변경하고, 비즈니스 규칙을 검증하는 책임을 가진다.
    /**
     * 배송지 변경 로직 (비즈니스 규칙 포함)
     */
    fun updateShippingInfo(newAddress: String) {
        // [규칙] 이미 배송 시작된 주문은 배송지를 변경할 수 없다.
        if (this.status != OrderStatus.PENDING) {
            throw IllegalStateException("배송이 시작된 주문은 주소를 변경할 수 없습니다.")
        }
        this.shippingAddress = newAddress
    }

    /**
     * 주문 취소 로직 (비즈니스 규칙 포함)
     */
    fun cancel() {
        // [규칙] 이미 배송 시작된 주문은 취소할 수 없다.
        if (this.status != OrderStatus.PENDING) {
            throw IllegalStateException("배송이 시작된 주문은 취소할 수 없습니다.")
        }
        this.status = OrderStatus.CANCELLED
    }

    /**
     * 주문 확정 로직
     */
    fun confirm() {
        this.status = OrderStatus.CONFIRMED
    }
}

enum class OrderStatus { PENDING, PAID, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
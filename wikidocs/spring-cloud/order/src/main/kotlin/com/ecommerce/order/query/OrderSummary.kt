package com.ecommerce.order.query;

import com.ecommerce.order.domain.OrderStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 읽기 전용 모델 (DTO이자 JPA 엔티티)
 * 'orders' 테이블이 아닌, 별도의 'order_summaries' 테이블에 저장된다.
 */
@Entity
@Table(name = "order_summaries")
class OrderSummary(
    @Id
    val orderId: Long, // 쓰기 모델의 ID와 동일한 값을 사용

    val memberId: Long,

    // 비정규화된 데이터: 쓰기 모델에는 없지만, 조회를 위해 미리 계산/조인해 둔 값
    val productName: String,

    val totalAmount:BigDecimal,

    @Enumerated(EnumType.STRING)
    var status:OrderStatus, // 이 상태는 쓰기 모델의 변경에 따라 '결과적으로' 동기화됨

    val orderedAt:LocalDateTime
)
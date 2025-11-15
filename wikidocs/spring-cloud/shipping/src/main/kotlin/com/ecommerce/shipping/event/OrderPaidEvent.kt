package com.ecommerce.shipping.event

import java.math.BigDecimal

// order-service의 이벤트 DTO 동일하게 작성
data class OrderPaidEvent(
    val orderId: Long,
    val memberId: Long,
    val totalAmount: BigDecimal,
    val orderLines: List<OrderLineItem>,
) {
    data class OrderLineItem(
        val productId: Long,
        val quantity: Int,
    )
}

// 실제 프로덕션 프로젝트에서는 이처럼 중요한 DTO를 각 서비스에 복사 붙여넣기 하는 대신, common-events.jar와 같은 공유 라이브러리로 만들어 양쪽 서비스가 모두 의존하도록 관리
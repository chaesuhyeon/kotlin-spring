package com.ecommerce.product.service

import com.ecommerce.product.event.StockDecreaseFailedEvent
import com.ecommerce.product.event.StockDecreasedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ProductEventProducer (
    private val kafkaTemplate: KafkaTemplate<String, StockDecreasedEvent>
){
    fun sendStockDecreasedEvent(event: StockDecreasedEvent) {}
    fun sendStockDecreaseFailedEvent(event: StockDecreaseFailedEvent) {}
}
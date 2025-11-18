package com.jinjindole2.jinpro.promotion.adapter.out.kafka.producer

import com.jinjindole2.jinpro.promotion.application.port.out.PromotionEventPort
import com.jinjindole2.jinpro.promotion.domain.event.AddPromotionJoinTypeEvent
import com.jinjindole2.jinpro.promotion.domain.event.JoinPromotionEvent
import com.jinjindole2.jinpro.promotion.domain.event.RegisterPromotionEvent
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Component

@Component
class PromotionEventProducer (
    private val kafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, Any>
) : PromotionEventPort {

    override suspend fun publishRegisterPromotionEvent(event: RegisterPromotionEvent) {
        kafkaProducerTemplate.send("promotion-register", event).awaitSingle()
    }

    override suspend fun publishJoinPromotionEvent(event: JoinPromotionEvent) {
        kafkaProducerTemplate.send("promotion-join", event).awaitSingle()
    }

    override suspend fun publishAddPromotionJoinTypeEvent(event: AddPromotionJoinTypeEvent) {
        kafkaProducerTemplate.send("promotion-add-join-type", event).awaitSingle()
    }
}
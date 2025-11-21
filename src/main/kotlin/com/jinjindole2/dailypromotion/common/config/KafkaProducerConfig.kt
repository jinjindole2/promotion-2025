package com.jinjindole2.dailypromotion.common.config

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaProducerConfig (
    private val kafkaAdmin: KafkaAdmin
        ) {

    @PostConstruct
    fun init() {
        kafkaAdmin.createOrModifyTopics(
            //TopicBuilder.name("card-issue").partitions(2).replicas(2).build()
            TopicBuilder.name("promotion-register")/*.partitions(2).replicas(2)*/.build(),
            TopicBuilder.name("promotion-join")/*.partitions(2).replicas(2)*/.build(),
            TopicBuilder.name("promotion-add-join-type")/*.partitions(2).replicas(2)*/.build()
        )
    }

    @Bean
    fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, Any> {
        val props = properties.buildProducerProperties()
        return ReactiveKafkaProducerTemplate(SenderOptions.create<String, Any>(props))
    }
}
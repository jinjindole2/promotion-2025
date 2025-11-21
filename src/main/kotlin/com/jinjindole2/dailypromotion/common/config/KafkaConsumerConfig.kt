package com.jinjindole2.dailypromotion.common.config

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.kafka.receiver.ReceiverOptions
import java.time.Duration

@Configuration
class KafkaConsumerConfig {

    @Bean
    fun kafkaReceiverOptions(
        kafkaProperties: KafkaProperties
    ): ReceiverOptions<String, String>? {
        val basicReceiverOptions: ReceiverOptions<String, String> =
            ReceiverOptions.create<String, String>(kafkaProperties.buildConsumerProperties())
        basicReceiverOptions.pollTimeout(Duration.ofSeconds(6))
        return basicReceiverOptions.subscription(listOf("adv-reward"))
    }

    @Bean
    fun reactiveConsumer(kafkaReceiverOptions: ReceiverOptions<String, String>): ReactiveKafkaConsumerTemplate<String, String> {
        return ReactiveKafkaConsumerTemplate<String, String>(kafkaReceiverOptions)
    }
}
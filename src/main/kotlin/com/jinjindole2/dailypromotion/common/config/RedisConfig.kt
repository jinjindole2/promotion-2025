package com.jinjindole2.dailypromotion.common.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean(name=["reactRedisTemplate"])
    @Primary
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Any> {
        val objectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        val context = RedisSerializationContext
            .newSerializationContext<String, Any>(StringRedisSerializer())
            .value(valueSerializer)
            .build()

        return ReactiveRedisTemplate(factory, context)
    }

    @Bean(name = ["reactiveStringRedisTemplate"])
    @Primary
    fun reactiveStringRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {
        val context = RedisSerializationContext
            .newSerializationContext<String, String>(StringRedisSerializer())
            .value(StringRedisSerializer())
            .build()

        return ReactiveRedisTemplate(factory, context)
    }

    @Bean
    @Primary
    fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val objectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfBaseType(Any::class.java).build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
            )

        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(factory)
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        return template
    }
}
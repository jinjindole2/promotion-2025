package com.jinjindole2.dailypromotion.common.config.lock

import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Component
import java.util.concurrent.TimeoutException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
class Locker(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>,
) {
    private val ops: ReactiveValueOperations<String, Any> = reactiveRedisTemplate.opsForValue()

    suspend fun <T> lock(key: String, fn: suspend () -> T): T {
        if( ! obtainLock(key) )
            throw TimeoutException("fail to obtain lock (key: $key)")
        try {
            return fn()
        } finally {
            unlock(key)
        }
    }

    private suspend fun obtainLock(key: String): Boolean {
        val start = System.nanoTime()
        while(! ops.setIfAbsent(key, "lock",10.seconds.toJavaDuration()).awaitSingle()) {
            delay(100.milliseconds)
            if((System.nanoTime() - start).nanoseconds >= 3.seconds)
                return false
        }
        return true
    }

    private suspend fun unlock(key: String) {
        reactiveRedisTemplate.delete(key).awaitSingle()
    }

}


package com.jinjindole2.dailypromotion

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
//@EnableScheduling
@EnableKafka
class DailyPromotionApplication

fun main(args: Array<String>) {
    runApplication<DailyPromotionApplication>(*args)
}

package com.jinjindole2.jinpro

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
@EnableKafka
class JinProApplication

fun main(args: Array<String>) {
    runApplication<JinProApplication>(*args)
}

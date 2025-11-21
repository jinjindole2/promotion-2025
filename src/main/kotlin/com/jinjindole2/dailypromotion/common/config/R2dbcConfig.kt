package com.jinjindole2.dailypromotion.common.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator

@Configuration
class R2dbcConfig {

    @Bean("transactionManager")  // 명시적으로 이름 지정 (MySQL/R2DBC용)
    fun transactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }

    @Bean
    fun transactionalOperator(transactionManager: ReactiveTransactionManager): TransactionalOperator {
        return TransactionalOperator.create(transactionManager)
    }
}
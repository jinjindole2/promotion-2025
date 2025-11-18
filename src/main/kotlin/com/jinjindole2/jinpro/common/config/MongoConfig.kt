package com.jinjindole2.jinpro.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager

@Configuration
class MongoConfig {

    @Bean("mongoTransactionManager")
    fun mongoTransactionManager(
        reactiveMongoDatabaseFactory: ReactiveMongoDatabaseFactory
    ): ReactiveMongoTransactionManager {
        return ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory)
    }
}
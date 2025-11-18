plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    id("org.sonarqube") version "4.4.1.3373"
    id("jacoco")
}

group = "com.jinprocorp"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Core
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    //implementation("org.springframework.boot:spring-boot-starter-security")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.8.1")

    // Database
    implementation("io.asyncer:r2dbc-mysql:1.1.3")
    implementation("io.r2dbc:r2dbc-pool:1.0.1.RELEASE")
    runtimeOnly("com.mysql:mysql-connector-j:8.3.0")
    
    // MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

    // Kafka
    implementation("io.projectreactor.kafka:reactor-kafka:1.3.21")
    implementation("org.springframework.kafka:spring-kafka:3.2.4")
    implementation("org.apache.kafka:kafka-streams:3.8.0")

    // Mapping
    implementation("org.mapstruct:mapstruct:1.6.0")
    kapt("org.mapstruct:mapstruct-processor:1.6.0")
    kaptTest("org.mapstruct:mapstruct-processor:1.6.0")

    // Logging & Monitoring
    implementation("io.micrometer:context-propagation:1.1.1")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")

    // Netty (for Apple Silicon compatibility)
    if (System.getProperty("os.arch") == "aarch64" && System.getProperty("os.name").contains("Mac")) {
        implementation("io.netty:netty-resolver-dns-native-macos:4.1.113.Final:osx-aarch_64")
    }

    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Test - Mockk
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("io.mockk:mockk-jvm:1.13.12")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

    // Test - Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")

    // Test - TestContainers
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.1"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // Test - H2 Database
    testRuntimeOnly("com.h2database:h2:2.2.224")
    testRuntimeOnly("io.r2dbc:r2dbc-h2:1.0.0.RELEASE")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

// Security vulnerability scanning
/*tasks.register("securityCheck") {
    doLast {
        println("Running security vulnerability check...")
        // Add OWASP dependency check or similar tools
    }
}*/

// Build optimizations
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = "17"
    }
}

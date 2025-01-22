package com.aneesakunju.fxrates.controller

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import org.springframework.web.client.RestClient

/**
 * Base class for integration tests. It bootstraps Spring Boot application with random port and
 * testcontainers. It provides a [RestClient] setup to make HTTP requests to the application, which
 * can be used in tests.
 */
@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestConstructor(autowireMode = ALL)
@TestInstance(PER_CLASS)
abstract class BaseIntegrationTest {

    @LocalServerPort protected var serverPort: Int = 0

    @Value("\${server.servlet.context-path}") protected lateinit var contextPath: String

    protected val restClient: RestClient by lazy {
        RestClient.builder().baseUrl("http://localhost:$serverPort/$contextPath").build()
    }
}

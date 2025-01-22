package com.aneesakunju.fxrates.controller

import com.aneesakunju.fxrates.config.controller.CurrenciesController.Companion.CURRENCIES_PATH
import com.aneesakunju.fxrates.dto.CurrencyV1
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.OK
import org.springframework.web.client.toEntity

class CurrenciesControllerIT : BaseIntegrationTest() {

    @Test
    fun `should successfully retrieve all currencies`() {
        val response = restClient.get().uri(CURRENCIES_PATH).retrieve().toEntity<List<CurrencyV1>>()
        response.statusCode shouldBe OK
        response.body.shouldNotBeNull().shouldContain(CurrencyV1("USD", "US Dollar"))
    }
}

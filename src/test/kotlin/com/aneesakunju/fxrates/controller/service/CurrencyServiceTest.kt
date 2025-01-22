package com.aneesakunju.fxrates.controller.service

import com.aneesakunju.fxrates.config.domain.Currency
import com.aneesakunju.fxrates.config.repository.currency.CurrencyRepository
import com.aneesakunju.fxrates.config.service.CurrencyService
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class CurrencyServiceTest {

    @Test
    fun `should return currencies from repository`() {
        val repository = mockk<CurrencyRepository>()

        val currencies = listOf(Currency(id = 1.toBigInteger(), code = "USD", name = "US Dollar"))
        every { repository.getAllCurrencies() } returns currencies

        val service = CurrencyService(repository)

        service.getAllCurrencies() shouldBe currencies
    }

    @Test
    fun `should allow repository exception to bubble up`() {
        val repository = mockk<CurrencyRepository>()

        val ex = RuntimeException("Bang!")

        every { repository.getAllCurrencies() } throws ex

        val service = CurrencyService(repository)

        shouldThrowAny { service.getAllCurrencies() } shouldBe ex
    }
}

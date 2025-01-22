package com.aneesakunju.fxrates.controller.service

import com.aneesakunju.fxrates.config.domain.ConvertedAmount
import com.aneesakunju.fxrates.config.domain.Currency
import com.aneesakunju.fxrates.config.domain.CurrencyAmount
import com.aneesakunju.fxrates.config.domain.FxRate
import com.aneesakunju.fxrates.config.repository.fxrate.FxRateRepository
import com.aneesakunju.fxrates.config.service.FxRateService
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FxRateServiceTest {

    @Test
    fun `should return latest rate for GBP to USD from repository`() {
        val repository = mockk<FxRateRepository>()
        val fxRates = listOf(
            FxRate(
            rate = BigDecimal(1.274446),
            LocalDate.parse("2024-01-10", DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            baseCurrencyCode = "GBP",
            targetCurrencyCode = "USD"
            )
        )
        every { repository.getLatestFXRate(
            baseCurrency = "GBP",
            targetCurrency = "USD"
            ) } returns fxRates
        val service = FxRateService(repository)

        service.getFXRate(baseCurrency = "GBP",
            targetCurrency = "USD"
        ) shouldBe fxRates
    }

    @Test
    fun `should return rate on given date for GBP to USD from repository`() {
        val repository = mockk<FxRateRepository>()
        val date = LocalDate.parse("2024-01-02", DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val fxRates = listOf(
            FxRate(
            rate = BigDecimal(1.262243),
            effectiveDate = date,
            baseCurrencyCode = "GBP",
            targetCurrencyCode = "USD"
        )
        )
        every { repository.getFXRateByDate(
            baseCurrency = "GBP",
            targetCurrency = "USD",
            date = date
        ) } returns fxRates
        val service = FxRateService(repository)

        service.getFXRate(baseCurrency = "GBP",
            targetCurrency = "USD",
            date = date
        ) shouldBe fxRates
    }

    @Test
    fun `should return rates within a given date range for GBP to USD from repository`() {
        val repository = mockk<FxRateRepository>()
        val date1 = LocalDate.parse("2024-01-02", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val date2 = LocalDate.parse("2024-01-03", DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val fxRates = listOf(
            FxRate(
            rate = BigDecimal(1.262243),
            effectiveDate = date1,
            baseCurrencyCode = "GBP",
            targetCurrencyCode = "USD"
        ), FxRate(
            rate = BigDecimal(1.267345),
            effectiveDate = date2,
            baseCurrencyCode = "GBP",
            targetCurrencyCode = "USD"
        )
        )
        every { repository.getHistoricalFXRates(
            baseCurrency = "GBP",
            targetCurrency = "USD",
            startDate = date1,
            endDate = date2
        ) } returns fxRates
        val service = FxRateService(repository)

        service.getHistoricalFXRates(baseCurrency = "GBP",
            targetCurrency = "USD",
            startDate = date1,
            endDate = date2
        ) shouldBe fxRates
    }

    @Test
    fun `should return converted amount on given date for GBP to USD from repository`() {
        val repository = mockk<FxRateRepository>()
        val date = LocalDate.parse("2024-01-02", DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val baseCurrency = Currency(id = 3.toBigInteger(), code = "GBP", name = "British Pound")
        val targetCurrency = Currency(id = 1.toBigInteger(), code = "USD", name = "US Dollar")
        every { repository.getCurrencyNameByCode(
            currencyCode = "GBP"
        ) } returns baseCurrency
        every { repository.getCurrencyNameByCode(
            currencyCode = "USD"
        ) } returns targetCurrency

        val baseCurrencyAmount = CurrencyAmount(baseCurrency, BigDecimal(5))
        val targetCurrencyAmount = CurrencyAmount(targetCurrency, BigDecimal(5) * BigDecimal(1.262243))

        val fxRate = FxRate(
            rate = BigDecimal(1.262243),
            effectiveDate = date,
            baseCurrencyCode = "GBP",
            targetCurrencyCode = "USD"
        )
        val fxRates = listOf(fxRate)
        every { repository.getFXRateByDate(
            baseCurrency = "GBP",
            targetCurrency = "USD",
            date = date
        ) } returns fxRates

        val convertedAmount = ConvertedAmount(baseCurrencyAmount, targetCurrencyAmount, fxRate)
        val service = FxRateService(repository)

        service.getConvertedAmount(baseCurrency = "GBP",
            targetCurrency = "USD",
            amount = BigDecimal(5),
            date = date
        ) shouldBe convertedAmount

    }
}
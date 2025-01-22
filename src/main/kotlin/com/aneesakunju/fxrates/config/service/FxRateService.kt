package com.aneesakunju.fxrates.config.service

import com.aneesakunju.fxrates.config.domain.ConvertedAmount
import com.aneesakunju.fxrates.config.domain.CurrencyAmount
import com.aneesakunju.fxrates.config.domain.FxRate
import com.aneesakunju.fxrates.config.repository.fxrate.FxRateRepository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Transactional(readOnly = false)
class FxRateService(private val repository: FxRateRepository) {

    @Transactional(readOnly = true)
    fun getFXRate(baseCurrency: String, targetCurrency: String, date: LocalDate? = null): List<FxRate> {
        return if (date == null) {
            repository.getLatestFXRate(baseCurrency, targetCurrency)
        } else {
            repository.getFXRateByDate(baseCurrency, targetCurrency, date)
        }
    }

    @Transactional(readOnly = true)
    fun getHistoricalFXRates(baseCurrency: String, targetCurrency: String, startDate: LocalDate, endDate: LocalDate): List<FxRate> {
        return repository.getHistoricalFXRates(baseCurrency, targetCurrency, startDate, endDate)
    }

    @Transactional(readOnly = true)
    fun getConvertedAmount(baseCurrency: String, targetCurrency: String, amount: BigDecimal, date: LocalDate?): ConvertedAmount {
        return if (date == null) {
            val latestRate = repository.getLatestFXRate(baseCurrency, targetCurrency)
            createConvertedAmount(latestRate, amount)
        } else {
            val rate = repository.getFXRateByDate(baseCurrency, targetCurrency, date)
            createConvertedAmount(rate, amount)
        }
    }

    private fun createConvertedAmount(
        rate: List<FxRate>,
        amount: BigDecimal
    ): ConvertedAmount {
        if (rate.isEmpty() || rate[0].rate == null) {
            throw Exception("Error getting latest rate between base and target currencies.")
        }
        val baseCurrencyObj = repository.getCurrencyNameByCode(rate[0].baseCurrencyCode)
        val targetCurrencyObj = repository.getCurrencyNameByCode(rate[0].targetCurrencyCode)

        val baseCurrencyAmount = CurrencyAmount(baseCurrencyObj, amount)
        val targetCurrencyAmount = CurrencyAmount(targetCurrencyObj, amount * rate[0].rate)

        return ConvertedAmount(baseCurrencyAmount, targetCurrencyAmount, rate[0])
    }
}

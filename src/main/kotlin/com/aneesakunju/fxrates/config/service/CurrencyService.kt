package com.aneesakunju.fxrates.config.service

import com.aneesakunju.fxrates.config.domain.Currency
import com.aneesakunju.fxrates.config.repository.currency.CurrencyRepository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = false)
class CurrencyService(private val repository: CurrencyRepository) {

    @Transactional(readOnly = true)
    fun getAllCurrencies(): List<Currency> {
        return repository.getAllCurrencies()
    }
}

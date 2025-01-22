package com.aneesakunju.fxrates.config.controller

import com.aneesakunju.fxrates.dto.CurrencyV1
import com.aneesakunju.fxrates.config.service.CurrencyService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CurrenciesController(private val service: CurrencyService) {

    companion object {
        const val CURRENCIES_PATH = "/currencies"
    }

    @GetMapping(CURRENCIES_PATH)
    fun getAllCurrencies(): ResponseEntity<*> {
        return try {
                service.getAllCurrencies().map { CurrencyV1(code = it.code, name = it.name) }
            } catch (e: Exception) {
                ResponseEntity.status(500).body("An error occurred while fetching currencies")
            }
            .let { ok(it) }
    }
}

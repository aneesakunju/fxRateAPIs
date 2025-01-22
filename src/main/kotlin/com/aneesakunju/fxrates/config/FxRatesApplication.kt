package com.aneesakunju.fxrates.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class FxRatesApplication

fun main(args: Array<String>) {
    runApplication<FxRatesApplication>(*args)
}

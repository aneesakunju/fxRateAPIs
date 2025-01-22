package com.aneesakunju.fxrates.controller

import com.aneesakunju.fxrates.config.FxRatesApplication
import com.aneesakunju.fxrates.controller.TestcontainersConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<FxRatesApplication>().with(TestcontainersConfiguration::class).run(*args)
}

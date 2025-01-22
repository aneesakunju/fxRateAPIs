package com.aneesakunju.fxrates.config.domain

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate

data class FxRate(val rate: BigDecimal, val effectiveDate: LocalDate,
                  val baseCurrencyCode: String, val targetCurrencyCode: String)

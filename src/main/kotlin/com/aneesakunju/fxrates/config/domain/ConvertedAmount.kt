package com.aneesakunju.fxrates.config.domain
/**
 * 
 * @param base 
 * @param target 
 * @param fxRate 
 */
data class ConvertedAmount(val base: CurrencyAmount, val target: CurrencyAmount, val fxRate: FxRate)



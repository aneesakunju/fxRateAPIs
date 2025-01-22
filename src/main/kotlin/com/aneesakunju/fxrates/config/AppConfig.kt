package com.aneesakunju.fxrates.config

import com.aneesakunju.fxrates.config.repository.currency.CurrencyRepository
import com.aneesakunju.fxrates.config.repository.currency.JdbcCurrencyRepository
import com.aneesakunju.fxrates.config.repository.fxrate.FxRateRepository
import com.aneesakunju.fxrates.config.repository.fxrate.JdbcFxRateRepository
import com.aneesakunju.fxrates.config.service.CurrencyService
import com.aneesakunju.fxrates.config.service.FxRateService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class AppConfig {

    @Bean
    fun currencyService(currencyRepository: CurrencyRepository): CurrencyService {
        return CurrencyService(currencyRepository)
    }

    @Bean
    fun fxRateService(fxRateRepository: FxRateRepository): FxRateService {
        return FxRateService(fxRateRepository)
    }

    @Bean
    fun currencyRepository(jdbcTemplate: JdbcTemplate): CurrencyRepository {
        return JdbcCurrencyRepository(jdbcTemplate)
    }

    @Bean
    fun fxRateRepository(jdbcTemplate: JdbcTemplate): FxRateRepository {
        return JdbcFxRateRepository(jdbcTemplate)
    }
}

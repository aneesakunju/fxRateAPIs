package com.aneesakunju.fxrates.config.repository.currency

import com.aneesakunju.fxrates.config.domain.Currency
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

interface CurrencyRepository {
    fun getAllCurrencies(): List<Currency>
}

@Repository
class JdbcCurrencyRepository(private val jdbcTemplate: JdbcTemplate) : CurrencyRepository {
    companion object {
        private const val SELECT_CURRENCIES_SQL = "SELECT id, name, code FROM currencies"
    }

    override fun getAllCurrencies(): List<Currency> {
        return jdbcTemplate.query(SELECT_CURRENCIES_SQL) { rs, _ ->
            Currency(
                id = rs.getBigDecimal("id").toBigInteger(),
                code = rs.getString("code"),
                name = rs.getString("name"),
            )
        }
    }
}

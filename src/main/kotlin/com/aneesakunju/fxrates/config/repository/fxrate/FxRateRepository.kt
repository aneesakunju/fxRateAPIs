package com.aneesakunju.fxrates.config.repository.fxrate

import com.aneesakunju.fxrates.config.domain.Currency
import com.aneesakunju.fxrates.config.domain.FxRate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate

interface FxRateRepository {
    fun getLatestFXRate(baseCurrency: String, targetCurrency: String): List<FxRate>
    fun getFXRateByDate(baseCurrency: String, targetCurrency: String, date: LocalDate): List<FxRate>
    fun getHistoricalFXRates(baseCurrency: String, targetCurrency: String, startDate: LocalDate, endDate: LocalDate): List<FxRate>
    fun getCurrencyNameByCode(currencyCode: String): Currency
}

@Repository
class JdbcFxRateRepository(private val jdbcTemplate: JdbcTemplate) : FxRateRepository {
    companion object {
        private const val SELECT_LATEST_FX_RATE_SQL = """
    SELECT
        t.rate AS target_rate,
        b.rate AS base_rate,
        t.effective_date AS effective_date,
        c_base.code AS base_currency,
        c_target.code AS target_currency
    FROM
        fx_rate AS t
    JOIN
        fx_rate AS b ON t.effective_date = b.effective_date
    JOIN
        currencies AS c_base ON b.target_currency_id = c_base.id
    JOIN
        currencies AS c_target ON t.target_currency_id = c_target.id
    WHERE
        c_target.code = ?
        AND c_base.code = ?
    ORDER BY
        t.effective_date DESC
    LIMIT 1;
"""
        private const val SELECT_FX_RATE_BY_DATE_SQL = """
    SELECT
        t.rate AS target_rate,
        b.rate AS base_rate,
        t.effective_date AS effective_date,
        c_base.code AS base_currency,
        c_target.code AS target_currency
    FROM
        fx_rate AS t
    JOIN
        fx_rate AS b ON t.effective_date = b.effective_date
    JOIN
        currencies AS c_base ON b.target_currency_id = c_base.id
    JOIN
        currencies AS c_target ON t.target_currency_id = c_target.id
    WHERE
        c_target.code = ?
        AND c_base.code = ?
        AND t.effective_date = ?
"""
        private const val SELECT_FX_RATE_BY_DATE_RANGE_SQL = """
    SELECT
        t.rate AS target_rate,
        b.rate AS base_rate,
        t.effective_date AS effective_date,
        c_base.code AS base_currency,
        c_target.code AS target_currency
    FROM
        fx_rate AS t
    JOIN
        fx_rate AS b ON t.effective_date = b.effective_date
    JOIN
        currencies AS c_base ON b.target_currency_id = c_base.id
    JOIN
        currencies AS c_target ON t.target_currency_id = c_target.id
    WHERE
        c_target.code = ?
        AND c_base.code = ?
        AND t.effective_date >= ? AND t.effective_date <= ?
    ORDER BY
        t.effective_date DESC
"""
        private const val SELECT_CURRENCY_NAME_SQL = """
    SELECT
        id, name, code
    FROM
        currencies 
    WHERE
        code = ?
    LIMIT 1
"""
    }
    override fun getLatestFXRate(baseCurrency: String, targetCurrency: String): List<FxRate> {
        val fxRates = jdbcTemplate.query(
            SELECT_LATEST_FX_RATE_SQL,
            { rs, _ ->
                FxRate(
                    rate = rs.getBigDecimal("target_rate")/rs.getBigDecimal("base_rate"),
                    effectiveDate = rs.getDate("effective_date").toLocalDate(),
                    baseCurrencyCode = rs.getString("base_currency"),
                    targetCurrencyCode = rs.getString("target_currency")
                )
            },
            targetCurrency,
            baseCurrency
        )
        if (fxRates == null || fxRates.isEmpty()) {
            throw IllegalStateException("No FX rate found for $baseCurrency to $targetCurrency")
        }

        return fxRates
    }

    override fun getFXRateByDate(baseCurrency: String, targetCurrency: String, date: LocalDate): List<FxRate> {
        val fxRates = jdbcTemplate.query(
            SELECT_FX_RATE_BY_DATE_SQL,
            { rs, _ ->
                FxRate(
                    rate = rs.getBigDecimal("target_rate")/rs.getBigDecimal("base_rate"),
                    effectiveDate = rs.getDate("effective_date").toLocalDate(),
                    baseCurrencyCode = rs.getString("base_currency"),
                    targetCurrencyCode = rs.getString("target_currency")
                )
            },
            targetCurrency,
            baseCurrency,
            date
        )

        if (fxRates == null || fxRates.isEmpty()) {
            throw IllegalStateException("No FX rate found for $baseCurrency to $targetCurrency for $date")
        }

        return fxRates
    }

    override fun getHistoricalFXRates(
        baseCurrency: String,
        targetCurrency: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<FxRate> {
        val fxRates = jdbcTemplate.query(
            SELECT_FX_RATE_BY_DATE_RANGE_SQL,
            { rs, _ ->
                FxRate(
                    rate = rs.getBigDecimal("target_rate")/rs.getBigDecimal("base_rate"),
                    effectiveDate = rs.getDate("effective_date").toLocalDate(),
                    baseCurrencyCode = rs.getString("base_currency"),
                    targetCurrencyCode = rs.getString("target_currency")
                )
            },
            targetCurrency,
            baseCurrency,
            startDate,
            endDate
        )

        if (fxRates == null || fxRates.isEmpty()) {
            throw IllegalStateException("No FX rate found for $baseCurrency to $targetCurrency between $startDate and $endDate")
        }

        return fxRates
    }

    override fun getCurrencyNameByCode(currencyCode: String): Currency {
        val currency = jdbcTemplate.query(
            SELECT_CURRENCY_NAME_SQL,
            { rs, _ ->
                Currency(
                    id = rs.getBigDecimal("id").toBigInteger(),
                    name = rs.getString("name"),
                    code = rs.getString("code")
                )
            },
            currencyCode
        )

        if (currency.isNullOrEmpty()) {
            throw IllegalStateException("No currency found for $currencyCode")
        }

        return currency[0]
    }

}
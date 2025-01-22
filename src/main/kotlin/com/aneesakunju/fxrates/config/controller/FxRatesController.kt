package com.aneesakunju.fxrates.config.controller

import com.aneesakunju.fxrates.dto.*
import com.aneesakunju.fxrates.config.service.FxRateService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.http.ResponseEntity
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.io.StringWriter

@RestController
class FxRatesController(private val service: FxRateService) {
    companion object {
        const val FX_RATE_PATH = "/{baseCurrency}/{targetCurrency}"
        const val FX_RATE_HISTORY_PATH = "$FX_RATE_PATH/history"
        const val CONVERT_AMOUNT_PATH = "$FX_RATE_PATH/convert"
    }

    @GetMapping(FX_RATE_PATH)
    fun getFXRate(
        @Size(min = 3, max = 3) @PathVariable("baseCurrency") baseCurrency: String,
        @Size(min = 3, max = 3) @PathVariable("targetCurrency") targetCurrency: String,
        @Valid
        @RequestParam(value = "date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd") date: java.time.LocalDate?
    ): ResponseEntity<*> {
        return try {
            val fxRateV1List = service.getFXRate(baseCurrency, targetCurrency, date).map {
                FxRateV1(
                    rate = it.rate,
                    effectiveDate = it.effectiveDate,
                    baseCurrencyCode = it.baseCurrencyCode,
                    targetCurrencyCode = it.targetCurrencyCode
                )
            }
            ResponseEntity.ok(fxRateV1List)
        } catch (e: Exception) {
            val errorResponse = ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                errorMessage = "An error occurred while fetching fx rates: ${e.message}"
            )
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(errorResponse)
        }
    }

    @GetMapping(CONVERT_AMOUNT_PATH)
    fun convertCurrency(
        @Size(min = 3, max = 3) @PathVariable("baseCurrency") baseCurrency: kotlin.String,
        @Size(min = 3, max = 3) @PathVariable("targetCurrency") targetCurrency: kotlin.String,
        @NotNull @Valid @RequestParam(value = "amount", required = true) amount: java.math.BigDecimal,
        @Valid
        @RequestParam(value = "date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd") date: java.time.LocalDate?

    ): ResponseEntity<*> {
        return try {
            val convertedAmount = service.getConvertedAmount(baseCurrency, targetCurrency, amount, date)
            val baseCurrencyV1 =
                CurrencyV1(code = convertedAmount.base.currency.code, name = convertedAmount.base.currency.name)
            val targetCurrencyV1 =
                CurrencyV1(code = convertedAmount.target.currency.code, name = convertedAmount.target.currency.name)
            val baseCurrencyAmount = CurrencyAmountV1(baseCurrencyV1, amount)
            val targetCurrencyAmount = CurrencyAmountV1(targetCurrencyV1, convertedAmount.target.amount)
            val fxRateV1 = FxRateV1(
                convertedAmount.fxRate.rate, convertedAmount.fxRate.effectiveDate,
                convertedAmount.fxRate.baseCurrencyCode, convertedAmount.fxRate.targetCurrencyCode
            )
            ResponseEntity.ok(ConvertedAmountV1(baseCurrencyAmount, targetCurrencyAmount, fxRateV1))
        } catch (e: Exception) {
            val errorResponse = ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                errorMessage = "An error occurred while converting amount: ${e.message}"
            )
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(errorResponse)
        }
    }

    @GetMapping(FX_RATE_HISTORY_PATH)
    fun getHistoricalFXRates(
        @RequestHeader(HttpHeaders.ACCEPT, required = false) acceptHeader: String?,
        @Size(min = 3, max = 3) @PathVariable("baseCurrency") baseCurrency: String,
        @Size(min = 3, max = 3) @PathVariable("targetCurrency") targetCurrency: String,
        @NotNull @Valid
        @RequestParam(value = "startDate", required = true)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd") startDate: java.time.LocalDate,
        @NotNull @Valid
        @RequestParam(value = "endDate", required = true)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd") endDate: java.time.LocalDate
    ): ResponseEntity<*> {
        return try {
            val fxRates: List<FxRateV1> = service.getHistoricalFXRates(baseCurrency, targetCurrency, startDate, endDate)
                .map {
                    FxRateV1(
                        rate = it.rate,
                        effectiveDate = it.effectiveDate,
                        baseCurrencyCode = it.baseCurrencyCode,
                        targetCurrencyCode = it.targetCurrencyCode
                    )
                }
            if (acceptHeader?.contains("text/csv", ignoreCase = true) == true) {
                val csvContent = convertFxRateV1ToCsv(fxRates)
                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                    .body(csvContent)
            } else {
                ResponseEntity.ok(fxRates)
            }
        } catch (e: Exception) {
            if (acceptHeader?.contains("text/csv", ignoreCase = true) == true) {
                ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body("An error occurred while fetching fx rates: ${e.message}")
            } else {
                val errorResponse = ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    errorMessage = "An error occurred while converting amount: ${e.message}"
                )
                ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(errorResponse)
            }
        }
    }

    private fun convertFxRateV1ToCsv(fxRates: List<FxRateV1>): String {
        val writer = StringWriter()
        val csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader("rate", "effectiveDate", "baseCurrencyCode", "targetCurrencyCode")
            .build()
        val csvPrinter = CSVPrinter(writer, csvFormat)
        try {
            fxRates.forEach { fxRate ->
                csvPrinter.printRecord(
                    fxRate.rate.toString(),
                    fxRate.effectiveDate.toString(),
                    fxRate.baseCurrencyCode.toString(),
                    fxRate.targetCurrencyCode.toString()
                )
            }
        } catch (e: Exception) {
            throw RuntimeException("Error with generating CSV content: ${e.message}", e)
        } finally {
            try {
                csvPrinter.close()
            } catch (closeEx: Exception) {
                println("Warning: Error while closing CSVPrinter: ${closeEx.message}")
            }
        }
        return writer.toString()
    }
}

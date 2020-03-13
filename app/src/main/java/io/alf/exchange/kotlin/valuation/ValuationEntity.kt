package io.cex.exchange.kotlin.valuation

import androidx.annotation.Keep

/**
 * @param[currencyName] 通用名称，e.g `US Dollar`
 * @param[currencyCode] 货币限定符，如 `USD`
 * @param[currencyMark] 货币符号，如 `$`
 * @param[exchangeRate] 该货币除以 USD 的汇率，如 `6.87`.
 */
@Keep
data class ValuationEntity(val currencyName: String = "", val currencyCode: String = "", val currencyMark: String = "",
                           val currencyIcon: String? = "",
                           val exchangeRate: String? = "1.0",
                           var isSelected: Boolean = false)

/**
 * 汇率及默认法币接口返回实体类。
 *
 * @property[defaultCurrency] 默认法币信息
 * @property[exchangeRate] 交易汇率信息
 */
@Keep
data class ValuationEntityWrapper(val defaultCurrency: ValuationEntity? = null,
                                  val exchangeRate: List<ValuationEntity>? = null)
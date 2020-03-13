package io.cex.exchange.kotlin.coreutil

import io.alf.exchange.App
import io.alf.exchange.R
import io.cex.exchange.kotlin.valuation.ValuationEntity
import io.cex.exchange.kotlin.valuation.ValuationPresenter


/**
 * 获取默认法币信息。以服务器配置为准，默认情况下为美元。
 *
 * @see [ValuationEntityWrapper.defaultCurrency]
 */
fun defaultLegalCurrency(): ValuationEntity {
    val default = ValuationPresenter.readDefaultCurrency()
    return default
        ?: ValuationEntity(App.getContext().getString(R.string.valuation_default_fiat), "USD", "$")
}

/**
 * 通过本地汇率接口获取某个代码代表的法币基本信息。
 */
fun obtainLegalBean(currencyCode: String): ValuationEntity {
    val list = ValuationPresenter.readLocalList()
    return list.firstOrNull {
        it.currencyCode == currencyCode
    } ?: ValuationEntity(App.getContext().getString(R.string.valuation_default_fiat), "USD", "$")
}

/**
 * 判断某个币种是否为默认法币。
 * @param[input] 待判断的币种代码，例如 `EUR`。
 */
fun isDefaultLegalCurrency(input: String?): Boolean {
    return input.equals(defaultLegalCurrency().currencyCode, true)
}

/**
 * 判断该币对是否需要显示折算后的价格。
 *
 * 交易对的交易区为当前计价币，则不显示。
 *
 * @param[tradePair] 交易对的代码表示，如 `BTC/USD`。
 */
fun shouldDisplayValuatedPrice(tradePair: String?): Boolean {
    return ValuationPresenter.currentValuation().currencyCode != splitTradePair(tradePair).second
}
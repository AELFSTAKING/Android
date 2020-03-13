package io.cex.exchange.kotlin.coreutil

/**
 * 将输入字符串按照交易对的字符串描述方式拆分为两部分。
 *
 * @param[input] 交易对的字符串描述，如 `BTC/USD`。
 * @return 拆分结果，例如 {`BTC`, `USD`}. 如果拆分失败则两部分均为空（{"",""}）。
 */
fun splitTradePair(input: String?): Pair<String, String> {
    if ((input != null) && (input.isNotEmpty())) {
        val split = input.split("/")
        if (split.size == 2) {
            return split[0] to split[1]
        }
    }
    return "" to ""
}

/**
 * 判断某个交易对是否为 默认法币 交易对。
 *
 * @param[input] 交易对的字符串描述，如 `BTC/USD`。
 */
fun isDefaultLegalPair(input: String?): Boolean {
    return isDefaultLegalCurrency(splitTradePair(input).second)
}
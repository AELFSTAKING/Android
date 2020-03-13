package io.cex.mqtt;

public class MQTTTopic {
    // 分区行情
    public static final String QUOTATION_AREA = "stakingdex_area_quotation";
    // 分组行情
    public static final String QUOTATION_GROUP = "stakingdex_quotation_group";
    // 最新成交列列表
    public static final String TRANSACTION_DEAL = "stakingdex_transaction_deal_";
    // K线数据
    public static final String TRANSACTION_KLINE = "stakingdex_transaction_kline_";
    // 最新深度图数据
    public static final String TRANSACTION_DEPTH = "stakingdex_transaction_depth_";
    // 订单簿数据
    public static final String ORDER_BOOK = "stakingdex_order_book_";
    // 热⻔门交易易对数据
    public static final String HOT_SYMBOLS = "stakingdex_hot_symbols";
    // 今⽇交易对⾏情
    public static final String SYMBOL_QUOTATION = "stakingdex_symbol_quotation_";
    // 涨幅交易对
    public static final String INCREASE_SYMBOLS = "stakingdex_increase_symbols";

    public static String getQuotationGroup() {
        return QUOTATION_GROUP;
    }

    public static String getQuotationArea() {
        return QUOTATION_AREA;
    }

    public static String getTransactionDeal(String symbol) {
        return TRANSACTION_DEAL + symbol;
    }

    public static String getTransactionKline(String symbol, String range) {
        return TRANSACTION_KLINE + symbol + "_" + range;
    }

    public static String getTransactionDepth(String symbol) {
        return TRANSACTION_DEPTH + symbol;
    }

    public static String getOrderBook(String symbol, String step) {
        return ORDER_BOOK + symbol + "_" + step;
    }

    public static String getHotSymbols() {
        return HOT_SYMBOLS;
    }

    public static String getSymbolQuotation(String symbol) {
        return SYMBOL_QUOTATION + symbol;
    }

    public static String getIncreaseSymbols() {
        return INCREASE_SYMBOLS;
    }
}

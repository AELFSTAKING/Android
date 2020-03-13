package io.cex.mqtt.bean;

import java.util.List;

public class QuotationGroupBean {
    public long areaNo;
    public String areaName;
    public String group;
    public List<SymbolQuotation> list;

    public static class SymbolQuotation {
        /**
         * 是否显示 0 否 1是
         */
        public String isShow;
        // 金额
        public String amount;
        /**
         * 计价币种USD折合价格，这个用area实际上偏向于计价币种是一个交易区（计价币种交易区，一般为稳定币种：btc、eth等）
         */
        public String areaUsdPrice;
        /**
         * 涨跌：1涨、0平、-1跌
         **/
        public String direction;
        /**
         * k线最高价
         */
        public String max;
        /**
         * 最高价的  USD折合价格
         */
        public String maxUsdPrice;
        /**
         * k线最低价
         */
        public String min;
        /**
         * 最低价的 USD折合价格
         */
        public String minUsdPrice;
        /**
         * 收盘价
         */
        public String lastPrice;
        /**
         * 当前价格涨跌：1涨、0平、-1跌
         */
        public String priceDirection;
        // 成交数量
        public String quantity;
        public String symbol;
        /**
         * 收盘价USD折合价格
         */
        public String lastUsdPrice;
        /**
         * 涨跌比例（是小数点）
         */
        public String waveChange;
        /**
         * 涨跌百分比
         */
        public String wavePercent;
        /**
         * 涨跌值
         */
        public String wavePrice;
        public List<StepBean> steps;
    }
}

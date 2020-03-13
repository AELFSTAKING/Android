package io.cex.mqtt.bean;

public class KLineBean {
    // 开盘价
    public String first;
    // 收盘价
    public String last;
    // 最高价
    public String max;
    // 最低价
    public String min;
    // 成交量
    public String quantity;
    // 时间
    public long time;
    // 涨跌幅
    public String applies;
    // 成交额
    public String dealAmount;
    // 成交均价
    public String minuteAvg;
    // 交易代码
    public String symbol;
    // K线聚合时间(毫秒)
    public long range;
    // 涨跌幅(收盘价-开盘价)/开盘价，是一个比例
    public String waveChange;
    // 交易额
    public String amount;
}

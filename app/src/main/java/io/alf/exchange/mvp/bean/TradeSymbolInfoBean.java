package io.alf.exchange.mvp.bean;

import java.io.Serializable;

/**
 * 查询交易对相关信息
 */

public class TradeSymbolInfoBean implements Serializable {
    // 总计价格的精度，比如8表示在总计价格的时候是小数点后8位
    public int amountScale;
    public String availableBuy;
    public String availableSell;
    // 计价币种最大挂单量
    public String currencyCoinQuantityMax;
    // 计价币种最小挂单量
    public String currencyCoinQuantityMin;
    // 计价币种挂单支持的小数位数
    public int currencyCoinQuantityScale;
    // 限价单-挂单价格偏差范围，判断不能让价格偏差过大
    public String deviation;
    //
    public String latestPrice;
    public String maxAmount;
    public String minAmount;
    public String priceBestBuy;
    public String priceBestSell;
    // 显示价格时候的价格精度
    public int priceScale;
    // 交易币种的最大挂单量
    public String productCoinQuantityMax;
    // 交易币种的最小挂单量
    public String productCoinQuantityMin;
    // 交易币种挂单支持的小数位数，比如8表示支持的是小数点后8位
    public String productCoinQuantityScale;
    public String symbol;
    public int tradeStatus;
}

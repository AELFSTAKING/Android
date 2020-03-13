package io.alf.exchange.mvp.bean;

import java.util.List;

import io.alf.exchange.bean.DealDetailBean;

public class OrderDetailBean {
    public String action;
    public String address;
    public String amount;
    public String amountRemaining;
    public String chain;
    public String counterChain;
    public String counterChainAddress;
    public String counterCurrency;
    public String currency;
    public String fee;
    public String feeCurrency;
    public String fromClientType;
    public String groupCode;
    public String level;
    public String makerFeeRate;
    public String orderNo;
    public String orderType;
    public String priceAverage;
    public String priceLimit;
    public String productCode;
    public String quantity;
    public String quantityRemaining;
    public String state;
    public String status;
    public String symbol;
    public String takerFeeRate;
    public String txId;
    public String url;
    public long utcCreate;
    public long utcUpdate;
    public List<DealDetailBean> tradeDeals;

}

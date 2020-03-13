package io.cex.mqtt.bean;

import java.io.Serializable;

public class QuotationBean implements Serializable {
    public String symbol;
    public String isShow;
    public String highestPrice;
    public String highestUsdPrice;
    public String lowestPrice;
    public String lowestUsdPrice;
    public String lastPrice;
    public String lastUsdPrice;
    public String quantity;
    public String wavePrice;
    public String wavePercent;
    public String direction;

    // TODO: 2019/5/7 仅为cex_increase_symbols保留
    public String price;
    public String usdPrice;
}

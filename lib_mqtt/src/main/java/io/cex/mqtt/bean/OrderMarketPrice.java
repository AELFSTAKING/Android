package io.cex.mqtt.bean;

import com.google.gson.annotations.Expose;

public class OrderMarketPrice {
    public String amount;
    public String limitPrice;
    public String quantity;
    @Expose
    public double sum;
}

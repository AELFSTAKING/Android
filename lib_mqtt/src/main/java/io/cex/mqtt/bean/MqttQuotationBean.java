package io.cex.mqtt.bean;

import java.util.List;

public class MqttQuotationBean {
    public String symbol;
    public String price;
    public String priceDirection;
    public String usdPrice;
    public String wavePrice;
    public String wavePercent;
    public String quantity;
    public String maxPrice;
    public String minPrice;
    public String direction;
    public String areaUsdPrice;
    public List<StepBean> steps;
}

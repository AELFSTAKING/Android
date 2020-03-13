package io.cex.mqtt.bean;

import com.google.gson.annotations.Expose;

public class SellInfo {
    // 价格
    public String price;
    // 数量
    public String quantity;
    // 暂时忽略
    public String stepValue;
    @Expose
    public double sum;
}

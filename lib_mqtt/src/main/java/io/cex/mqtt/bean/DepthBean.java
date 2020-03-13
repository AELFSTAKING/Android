package io.cex.mqtt.bean;

import java.io.Serializable;
import java.util.List;

public class DepthBean implements Serializable {
    public String symbol;
    // 最大买单价
    public String bidsMaxPrice;
    // 卖最低价
    public String asksMinPrice;
    // 深度比例(暂时忽略)
    public String depthPercent;
    // x轴最小值(最小价格)
    public String xMinPrice;
    // x轴最大值(最大价格)
    public String xMaxPrice;
    // y轴最大数量
    public String yQuantity;
    // 最新买单数据列表
    public List<SellInfo> bids;
    // 最新卖单数据列表
    public List<SellInfo> asks;
}

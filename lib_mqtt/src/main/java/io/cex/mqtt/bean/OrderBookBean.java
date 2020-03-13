package io.cex.mqtt.bean;

import java.util.List;

public class OrderBookBean {
    public String symbol;
    public int limitSize;
    public long timestamp;
    public List<OrderMarketPrice> askList;
    public List<OrderMarketPrice> bidList;
    public String step;
    public String askQuantity;
    public String bidQuantity;
    public OrderMarketDealItem latestDeal;

    public static class OrderMarketDealItem {
        public String price;
        public String usdPrice;
        public int direction;
    }
}

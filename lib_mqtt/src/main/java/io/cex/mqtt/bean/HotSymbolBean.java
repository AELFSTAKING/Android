package io.cex.mqtt.bean;

import java.io.Serializable;
import java.util.List;

public class HotSymbolBean implements Serializable {
    public String symbol;
    public String isShow;
    public String price;
    public String quantity;
    public String amount;
    public String usdPrice;
    public String wavePrice;
    public String wavePercent;
    public String direction;
    public String priceDirection;
    public List<KLineBean> klineList;

    public static class KLineBean implements Serializable {
        public String price;
        public long time;
    }
}

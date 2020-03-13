package io.cex.mqtt.bean;

import java.util.List;

public class HotSymbolsBean {
    public String code;
    public String msg;
    public String traceId;
    public DataBean data;

    public static class DataBean {
        public List<HotBean> hotSymbols;

        public static class HotBean {
            public String symbol;
            public String price;
            public String quantity;
            public String amount;
            public String usdPrice;
            public String wavePrice;
            public String wavePercent;
            public String direction;
            public String priceDirection;
            public List<KLine> klineList;

            public static class KLine {
                public String price;
                public String time;
            }
        }
    }
}

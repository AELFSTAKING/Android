package com.guoziwei.klinelib.model;

import java.util.ArrayList;
import java.util.List;

public class RSV {

    double high = 0.0;
    double low = 0.0;
    double close = 0.0;
    /**
     * //KDJ(9,3.3),下面以该参数为例说明计算方法。
     * //9，3，3代表指标分析周期为9天，K值D值为3天
     * //RSV(9)=（今日收盘价－9日内最低价）÷（9日内最高价－9日内最低价）×100
     * //K(3日)=（当日RSV值+2*前一日K值）÷3
     * //D(3日)=（当日K值+2*前一日D值）÷3
     * //J=3K－2D
     * /
     **/
    private ArrayList<Double> rsv;
    private int n;

    public RSV(List<HisData> OHLCData, int m) {
        n = m;
        rsv = new ArrayList<Double>();
        double rs = 0.0;

        if (OHLCData != null && OHLCData.size() > 0) {

            for (int i = 0; i < OHLCData.size(); i++) {

                if (i < m - 1) {
                    rsv.add(0.00);
                    continue;
                }

                HisData oHLCEntity = OHLCData.get(i);
                high = oHLCEntity.getHigh();
                low = oHLCEntity.getLow();
                close = oHLCEntity.getClose();

                for (int j = 0; j < m; j++) {
                    HisData oHLCEntity1 = OHLCData.get(i - j);
                    high = high > oHLCEntity1.getHigh() ? high : oHLCEntity1.getHigh();
                    low = low < oHLCEntity1.getLow() ? low : oHLCEntity1.getLow();
                }

                if (high != low) {
                    rs = (close - low) / (high - low) * 100;
                    rsv.add(rs);
                } else {
                    rsv.add(0.00);
                }
            }

        }
    }

    public List<Double> getRSV() {
        return rsv;
    }
}


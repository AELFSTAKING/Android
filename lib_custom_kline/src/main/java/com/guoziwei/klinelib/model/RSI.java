package com.guoziwei.klinelib.model;

import java.util.ArrayList;

/**
 * Created by loro on 2017/3/7.
 */

public class RSI {

    private ArrayList<Double> RSIs;

    /**
     * @param kLineBeens
     * @param n          几日
     */
    public RSI(ArrayList<HisData> kLineBeens, int n) {
        this(kLineBeens, n, Double.NaN);
    }

    /**
     * @param kLineBeens
     * @param n          几日
     * @param defult     不足N日时的默认值
     */
    public RSI(ArrayList<HisData> kLineBeens, int n, double defult) {
        RSIs = new ArrayList<>();
        double sum = 0.0f;
        double dif = 0.0f;
        double rs = 0.0f;
        double rsi = 0.0f;
        int index = n - 1;
        if (kLineBeens != null && kLineBeens.size() > 0) {
            for (int i = 0; i < kLineBeens.size(); i++) {

                if (i >= index) {
                    Float[] wrs = getAAndB(i - index, i, (ArrayList<HisData>) kLineBeens);
                    sum = wrs[0];
                    dif = wrs[1];
                    double h = sum + dif;
                    rsi = sum / h * 100;
                }


                if (i < index) {
                    rsi = defult;
                }
                RSIs.add(rsi);
            }
        }
    }

    private Float[] getAAndB(Integer a, Integer b, ArrayList<HisData> kLineBeens) {
        if (a < 0)
            a = 0;
        float sum = 0.0f;
        float dif = 0.0f;
        float closeT, closeY;
        Float[] abs = new Float[2];
        for (int i = a; i <= b; i++) {
            if (i > 0) {
                closeT = (float) kLineBeens.get(i).getClose();
                closeY = (float) kLineBeens.get(i - 1).getClose();

                float c = closeT - closeY;
                if (c > 0) {
                    sum = sum + c;
                } else {
                    dif = dif + c;
                }
            }
        }

        dif = Math.abs(dif);

        abs[0] = sum;
        abs[1] = dif;
        return abs;
    }

    public ArrayList<Double> getRSIs() {
        return RSIs;
    }

    public Double getLastRSI() {
        if (RSIs == null) return 0d;
        return RSIs.get(RSIs.size() - 1);
    }
}

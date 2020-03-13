package com.guoziwei.klinelib.model;

import java.util.ArrayList;

/**
 * Created by loro on 2017/3/8.
 */

public class EXPMA {

    private ArrayList<Double> EXPMAs;

    public EXPMA(ArrayList<HisData> kLineBeens, int n) {
        EXPMAs = new ArrayList<>();

        double ema = 0.0f;
        double t = n + 1;
        double yz = 2 / t;
        if (kLineBeens != null && kLineBeens.size() > 0) {

            for (int i = 0; i < kLineBeens.size(); i++) {
                if (i == 0) {
                    ema = kLineBeens.get(i).getClose();
                } else {
                    ema = ((yz * kLineBeens.get(i).getClose()) + ((1 - yz) * ema));
//                    ema = (kLineBeens.get(i).close - ema) * (2 / (n + 1)) + ema;
                }
                EXPMAs.add(ema);
            }
        }
    }

    public ArrayList<Double> getEXPMAs() {
        return EXPMAs;
    }

    public Double getLastEXPMA() {
        if (EXPMAs != null && EXPMAs.size() > 0) {
            return EXPMAs.get(EXPMAs.size() - 1);
        } else {
            return Double.NaN;
        }

    }
}

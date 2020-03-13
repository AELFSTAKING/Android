package com.guoziwei.klinelib.model;

import java.util.ArrayList;

import static java.lang.Float.NaN;

/**
 * Created by loro on 2017/3/7.
 */

public class BOLL {

    private ArrayList<Float> UPs;
    private ArrayList<Float> MBs;
    private ArrayList<Float> DNs;

    /**
     * 得到BOLL指标
     *
     * @param kLineBeens
     * @param n
     */
    public BOLL(ArrayList<HisData> kLineBeens, int n) {
        this(kLineBeens, n, NaN);
    }

    /**
     * 得到BOLL指标
     *
     * @param kLineBeens
     * @param n
     * @param defult
     */
    public BOLL(ArrayList<HisData> kLineBeens, int n, float defult) {
        UPs = new ArrayList<>();
        MBs = new ArrayList<>();
        DNs = new ArrayList<>();

        float ma = 0.0f;
        float md = 0.0f;
        float mb = 0.0f;
        float up = 0.0f;
        float dn = 0.0f;

        if (kLineBeens != null && kLineBeens.size() > 0) {
            float closeSum = 0.0f;
            float sum = 0.0f;
            int index = 0;
            int index2 = n - 1;
            for (int i = 0; i < kLineBeens.size(); i++) {

                int k = i - n + 1;
                if (i >= n) {
                    index = 20;
                } else {
                    index = i + 1;
                }
                closeSum = getSumClose(k, i, kLineBeens);
                ma = closeSum / index;


                sum = getSum(k, i, ma, kLineBeens);
                md = (float) Math.sqrt(sum / index);
                mb = ma;
                //mb = (float) ((closeSum - kLineBeens.get(i).getClose()) / (index-1));
                up = mb + (2 * md);
                dn = mb - (2 * md);

                if (i < index2) {
                    mb = defult;
                    up = defult;
                    dn = defult;
                }

                UPs.add(up);
                MBs.add(mb);
                DNs.add(dn);
            }

        }

    }

    private Float getSum(Integer a, Integer b, Float ma, ArrayList<HisData> kLineBeens) {
        if (a < 0)
            a = 0;
        HisData kLineBean;
        float sum = 0.0f;
        for (int i = a; i <= b; i++) {
            kLineBean = kLineBeens.get(i);
            sum += ((kLineBean.getClose() - ma) * (kLineBean.getClose() - ma));
        }

        return sum;
    }


    private Float getSumClose(Integer a, Integer b, ArrayList<HisData> kLineBeens) {
        if (a < 0)
            a = 0;
        HisData kLineBean;
        float close = 0.0f;
        for (int i = a; i <= b; i++) {
            kLineBean = kLineBeens.get(i);
            close += kLineBean.getClose();
        }

        return close;
    }


    public ArrayList<Float> getUPs() {
        return UPs;
    }

    public Float getLastUP() {
        if (UPs != null && UPs.size() > 0) {
            return UPs.get(UPs.size() - 1);
        } else {
            return NaN;
        }
    }

    public ArrayList<Float> getMBs() {
        return MBs;
    }

    public Float getLastMB() {
        if (MBs != null && MBs.size() > 0) {
            return MBs.get(MBs.size() - 1);
        } else {
            return NaN;
        }
    }

    public ArrayList<Float> getDNs() {
        return DNs;
    }

    public Float getLastDN() {
        if (DNs != null && DNs.size() > 0) {
            return DNs.get(DNs.size() - 1);
        } else {
            return NaN;
        }
    }
}

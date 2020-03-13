package com.guoziwei.klinelib.model;

import android.util.Log;

import java.util.ArrayList;

import static java.lang.Float.NaN;

/**
 * Created by loro on 2017/3/7.
 */

public class BOLL2 {

    private ArrayList<Float> UPs;
    private ArrayList<Float> MBs;
    private ArrayList<Float> DNs;

    /**
     * 得到BOLL指标
     *
     * @param listma
     * @param n
     */
    public BOLL2(ArrayList<Double> listma, ArrayList<HisData> kLineBeens, int n) {
        this(listma, kLineBeens, n, NaN);
    }

    /**
     * 得到BOLL指标
     *
     * @param listma
     * @param n
     * @param defult
     */
    public BOLL2(ArrayList<Double> listma, ArrayList<HisData> kLineBeens, int n, float defult) {
        UPs = new ArrayList<>();
        MBs = new ArrayList<>();
        DNs = new ArrayList<>();

        double ma = 0.0f;
        double md = 0.0f;
        double mb = 0.0f;
        double up = 0.0f;
        double dn = 0.0f;

        if (listma != null && listma.size() > 0) {
            double closeSum = 0.0f;
            double sum = 0.0f;
            int index = 0;
            int index2 = n - 1;
            for (int i = 0; i < listma.size(); i++) {

                int k = i - n + 1;
                if (i >= n) {
                    index = 20;
                } else {
                    index = i + 1;
                }
                // closeSum = getSumClose(k, i, kLineBeens);
                ma = listma.get(i);
                Log.i("1234", "ma=" + ma);

                // ma = closeSum/index;
                sum = getSum(k, i, ma, kLineBeens);
                // md = DoubleArith.mul(DoubleArith.div(sum, (double) index), DoubleArith.div(sum, (double) index));
                md = Math.sqrt(sum / index);
                mb = ma;
                //mb = (float) ((closeSum - kLineBeens.get(i).getClose()) / (index-1));
                up = mb + (2 * md);
                dn = mb - (2 * md);

                if (i < index2) {
                    UPs.add(Float.NaN);
                    MBs.add(Float.NaN);
                    DNs.add(Float.NaN);
                } else {
                    UPs.add((float) up);
                    MBs.add((float) mb);
                    DNs.add((float) dn);
                }
            }

        }

    }

    private Double getSum(Integer a, Integer b, Double ma, ArrayList<HisData> kLineBeens) {
        if (a < 0)
            a = 0;
        HisData kLineBean;
        double sum = 0.0f;
        for (int i = a; i <= b; i++) {
            kLineBean = kLineBeens.get(i);
            /*if (Double.isNaN(ma)) ma = 0d;
            double close = Double.isNaN(kLineBean.getClose())?0d:kLineBean.getClose();
            sum = DoubleArith.add(sum, DoubleArith.mul(DoubleArith.sub(close, ma), DoubleArith.sub(close, ma)));
            // DoubleArith.mul(DoubleArith.sub(kLineBean.getClose(),ma),DoubleArith.sub(kLineBean.getClose(),ma));
            //sum +=DoubleArith.sub(kLineBean.getClose(),ma)*DoubleArith.sub(kLineBean.getClose(),ma);*/
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

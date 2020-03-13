package com.guoziwei.klinelib.model;

import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;

/**
 * chart data model
 */

public class HisData {

    private double close;
    private double high;
    private double low;
    private double open;
    private double vol;//成交量
    private double quantity;//成交量
    private long date;
    private double dealAmount;//成交额
    private double amountVol;
    // 成交均价
    private double avgPrice;
    private double total;
    private double maSum;
    private double ma5;
    private double ma10;
    private double ma20;

    private double dif;
    private double dea;
    private double macd;

    private double k;
    private double d;
    private double j;

    private double ema7;
    private double ema30;


    private double UPs;
    private double MBs;
    private double DNs;

    private double volDeepQuantity;//成交量
    private double volDeepPrice;//价格
    private BarEntry barDatasRSI;
    private double rsiData6;
    private double rsiData12;
    private double rsiData24;
    private double vma5;
    private double vma10;

    public HisData() {
    }
    public HisData(double open, double close, double high, double low, int vol, long date) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.vol = vol;
        this.date = date;
    }

    public double getVolDeepQuantity() {
        return volDeepQuantity;
    }


    public static boolean stopTest;

    public void setVolDeepQuantity(double volDeepQuantity) {
        if (!stopTest) {
            Log.i("Dupeng", "setVolDeepQuantity : " + volDeepQuantity);
        }
        this.volDeepQuantity = volDeepQuantity;
    }

    public double getVolDeepPrice() {
        return volDeepPrice;
    }

    public void setVolDeepPrice(double volDeepPrice) {
        if (!stopTest) {
            Log.i("Dupeng", "volDeepPrice : " + volDeepPrice);
        }
        this.volDeepPrice = volDeepPrice;
    }

    public BarEntry getBarDatasRSI() {
        return barDatasRSI;
    }

    public void setBarDatasRSI(BarEntry barDatasRSI) {
        this.barDatasRSI = barDatasRSI;
    }

    public double getDif() {
        return dif;
    }

    public void setDif(double dif) {
        this.dif = dif;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(double dealAmount) {
        this.dealAmount = dealAmount;
    }

    public double getAmountVol() {
        return amountVol;
    }

    public void setAmountVol(double amountVol) {
        this.amountVol = amountVol;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMa5() {
        return ma5;
    }

    public void setMa5(double ma5) {
        this.ma5 = ma5;
    }

    public double getMa10() {
        return ma10;
    }

    public void setMa10(double ma10) {
        this.ma10 = ma10;
    }

    public double getMa20() {
        return ma20;
    }

    public void setMa20(double ma20) {
        this.ma20 = ma20;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HisData data = (HisData) o;

        return date == data.date;
    }

    @Override
    public int hashCode() {
        return (int) (date ^ (date >>> 32));
    }

    public double getMaSum() {
        return maSum;
    }

    public void setMaSum(double maSum) {
        this.maSum = maSum;
    }

    public double getDea() {
        return dea;
    }

    public void setDea(double dea) {
        this.dea = dea;
    }

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getJ() {
        return j;
    }

    public void setJ(double j) {
        this.j = j;
    }

    public double getEma7() {
        return ema7;
    }

    public void setEma7(double ema7) {
        this.ema7 = ema7;
    }

    public double getEma30() {
        return ema30;
    }

    public void setEma30(double ema30) {
        this.ema30 = ema30;
    }

    public double getUPs() {
        return UPs;
    }

    public void setUPs(double UPs) {
        this.UPs = UPs;
    }

    public double getMBs() {
        return MBs;
    }

    public void setMBs(double MBs) {
        this.MBs = MBs;
    }

    public double getDNs() {
        return DNs;
    }

    public void setDNs(double DNs) {
        this.DNs = DNs;
    }


    public double getRsiData6() {
        return rsiData6;
    }

    public void setRsiData6(double rsiData6) {
        this.rsiData6 = rsiData6;
    }

    public double getRsiData12() {
        return rsiData12;
    }

    public void setRsiData12(double rsiData12) {
        this.rsiData12 = rsiData12;
    }

    public double getRsiData24() {
        return rsiData24;
    }

    public void setRsiData24(double rsiData24) {
        this.rsiData24 = rsiData24;
    }


    public double getVma5() {
        return vma5;
    }

    public void setVma5(double vma5) {
        this.vma5 = vma5;
    }

    public double getVma10() {
        return vma10;
    }

    public void setVma10(double vma10) {
        this.vma10 = vma10;
    }


    @Override
    public String toString() {
        return "HisData{" +
                "close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                ", vol=" + vol +
                ", date=" + date +
                ", amountVol=" + amountVol +
                ", avgPrice=" + avgPrice +
                ", total=" + total +
                ", maSum=" + maSum +
                ", ma5=" + ma5 +
                ", ma10=" + ma10 +
                ", ma20=" + ma20 +
                ", dif=" + dif +
                ", dea=" + dea +
                ", macd=" + macd +
                ", k=" + k +
                ", d=" + d +
                ", j=" + j +
                '}';
    }

}

package com.guoziwei.klinelib.util;

import android.util.Log;
import com.github.mikephil.charting.data.BarEntry;
import com.guoziwei.klinelib.model.*;

import java.util.ArrayList;
import java.util.List;

import static com.guoziwei.klinelib.model.VMA.getLastMA;

/**
 * Created by dell on 2017/11/9.
 */

public class DataUtils {


    private static int mMA5Index = 5;
    private static int mMA10Index = 10;
    private static int mMA20Index = 20;
    private static double mEMA7 = 7;
    private static double mEMA30 = 30;
    private static double emayz7 = 2 / (mEMA7 + 1);
    private static double emayz7_ = 0;
    private static double emayz30 = 2 / (mEMA30 + 1);
    private static double emayz30_ = 0;
    private static int mBOLL = 20;
    private static int mKDJ = 9;
    private static int mRSI6 = 6;
    private static int mRSI12 = 12;
    private static int mRSI24 = 24;

    /**
     * calculate average price and ma data
     */
    public static List<HisData> calculateHisData(List<HisData> list, HisData lastData) {

        //  long timeS = System.currentTimeMillis();

        List<Double> ma5List = calculateMA(5, list);
        List<Double> ma10List = calculateMA(10, list);
        List<Double> ma20List = calculateMA(20, list);

        MACD macd = new MACD(list);
        List<Double> bar = macd.getMACD();
        List<Double> dea = macd.getDEA();
        List<Double> dif = macd.getDIF();

        KDJ kdj = new KDJ(list);
        ArrayList<Double> d = kdj.getD();
        ArrayList<Double> k = kdj.getK();
        ArrayList<Double> j = kdj.getJ();

        EXPMA expma7 = new EXPMA((ArrayList<HisData>) list, 7);
        ArrayList<Double> expmas7 = expma7.getEXPMAs();

        EXPMA expma30 = new EXPMA((ArrayList<HisData>) list, 30);
        ArrayList<Double> expmas30 = expma30.getEXPMAs();

        BOLL2 boll = new BOLL2((ArrayList<Double>) ma20List, (ArrayList<HisData>) list, 20);
        ArrayList<Float> dns = boll.getDNs();
        ArrayList<Float> mbs = boll.getMBs();
        ArrayList<Float> ups = boll.getUPs();

        RSI rsi6 = new RSI((ArrayList<HisData>) list, 6);
        ArrayList<Double> rsi6List = rsi6.getRSIs();
        RSI rsi12 = new RSI((ArrayList<HisData>) list, 12);
        ArrayList<Double> rsi12List = rsi12.getRSIs();
        RSI rsi24 = new RSI((ArrayList<HisData>) list, 24);
        ArrayList<Double> rsi24List = rsi24.getRSIs();

        VMA vma5 = new VMA((ArrayList<HisData>) list, 5);
        ArrayList<Float> vma7List = vma5.getMAs();
        VMA vma10 = new VMA((ArrayList<HisData>) list, 10);
        ArrayList<Float> vma25List = vma10.getMAs();


        double amountVol = 0;
        if (lastData != null) {
            amountVol = lastData.getAmountVol();
        }
        for (int i = 0; i < list.size(); i++) {
            HisData hisData = list.get(i);

            hisData.setMa5(ma5List.get(i));
            hisData.setMa10(ma10List.get(i));
            hisData.setMa20(ma20List.get(i));

            hisData.setMacd(bar.get(i));
            hisData.setDea(dea.get(i));
            hisData.setDif(dif.get(i));

            hisData.setD(d.get(i));
            hisData.setK(k.get(i));
            hisData.setJ(j.get(i));

            hisData.setEma7(expmas7.get(i));
            hisData.setEma30(expmas30.get(i));

            hisData.setDNs(dns.get(i));
            hisData.setMBs(mbs.get(i));
            hisData.setUPs(ups.get(i));

            hisData.setRsiData6(rsi6List.get(i));
            hisData.setRsiData12(rsi12List.get(i));
            hisData.setRsiData24(rsi24List.get(i));
            hisData.setBarDatasRSI(new BarEntry(0, i));


            hisData.setVma5(vma7List.get(i));
            hisData.setVma10(vma25List.get(i));

            amountVol += hisData.getVol();
            hisData.setAmountVol(amountVol);

            //   Log.i("1234","==="+hisData.getAvgPrice());

           /* if (i > 0) {
                double total = hisData.getVol() * hisData.getClose() + list.get(i - 1).getTotal();
                hisData.setTotal(total);
                double avePrice = total / amountVol;
                hisData.setAvgPrice(avePrice);
            } else if (lastData != null) {
                double total = hisData.getVol() * hisData.getClose() + lastData.getTotal();
                hisData.setTotal(total);
                double avePrice = total / amountVol;
                hisData.setAvgPrice(avePrice);
            } else {
                hisData.setAmountVol(hisData.getVol());
               // hisData.setAvgPrice(hisData.getClose());
                hisData.setTotal(hisData.getAmountVol() * hisData.getAvePrice());
            }*/

        }
       /* long timeE = System.currentTimeMillis()-timeS;
        Log.i("1234","timeE0="+timeE);*/
        return list;
    }

    public static List<HisData> calculateHisData(List<HisData> list) {
        return calculateHisData(list, null);
    }

    /**
     * according to the history data list, calculate a new data
     */
    public static HisData calculateHisData(HisData newData, List<HisData> hisDatas) {


        hisDatas.add(newData);//加入新数据 再进行指标计算

        HisData lastData = hisDatas.get(hisDatas.size() - 1);
        double amountVol = lastData.getAmountVol();

        //sma
        newData.setMa5(calculateLastMA(5, hisDatas));
        newData.setMa10(calculateLastMA(10, hisDatas));
        newData.setMa20(calculateLastMA(20, hisDatas));

        //ema
        EXPMA expma7 = new EXPMA((ArrayList<HisData>) hisDatas, 7);
        newData.setEma7(expma7.getLastEXPMA());
        EXPMA expma30 = new EXPMA((ArrayList<HisData>) hisDatas, 30);
        newData.setEma30(expma30.getLastEXPMA());

        //boll
        BOLL boll = new BOLL((ArrayList<HisData>) hisDatas, 20);
        newData.setUPs(boll.getLastUP());
        newData.setMBs(boll.getLastMB());
        newData.setDNs(boll.getLastDN());

        //vol ma
        newData.setVma5(getLastMA((ArrayList<HisData>) hisDatas, 5));
        newData.setVma10(getLastMA((ArrayList<HisData>) hisDatas, 10));

        //macd
        MACD macd = new MACD(hisDatas);
        List<Double> bar = macd.getMACD();
        newData.setMacd(bar.get(bar.size() - 1));
        List<Double> dea = macd.getDEA();
        newData.setDea(dea.get(dea.size() - 1));
        List<Double> dif = macd.getDIF();
        newData.setDif(dif.get(dif.size() - 1));

        //kdj
        KDJ kdj = new KDJ(hisDatas);
        ArrayList<Double> d = kdj.getD();
        newData.setD(d.get(d.size() - 1));
        ArrayList<Double> k = kdj.getK();
        newData.setK(k.get(k.size() - 1));
        ArrayList<Double> j = kdj.getJ();
        newData.setJ(j.get(j.size() - 1));

        //rsi
        RSI rsi6 = new RSI((ArrayList<HisData>) hisDatas, 6);
        newData.setRsiData6(rsi6.getLastRSI());

        RSI rsi12 = new RSI((ArrayList<HisData>) hisDatas, 12);
        newData.setRsiData6(rsi12.getLastRSI());

        RSI rsi24 = new RSI((ArrayList<HisData>) hisDatas, 24);
        newData.setRsiData6(rsi24.getLastRSI());

        amountVol += newData.getVol();
        newData.setAmountVol(amountVol);
        double total = newData.getVol() * newData.getClose() + lastData.getTotal();
        newData.setTotal(total);

        //double avePrice = total / amountVol;
        // newData.setAvgPrice(avePrice);


        return newData;
    }

    /**
     * calculate MA value, return a double list
     *
     * @param dayCount for example: 5, 10, 20, 30
     */
    public static List<Double> calculateMA(int dayCount, List<HisData> data) {
        dayCount--;
        List<Double> result = new ArrayList<>(data.size());
        for (int i = 0, len = data.size(); i < len; i++) {
            if (i < dayCount) {
                result.add(Double.NaN);
                continue;
            }
            double sum = 0;
            for (int j = 0; j < (dayCount + 1); j++) {
                sum += data.get(i - j).getClose();
            }
            result.add(+(sum / (dayCount + 1)));
        }
        return result;
    }

    /**
     * calculate last MA value, return a double value
     */
    public static double calculateLastMA(int dayCount, List<HisData> data) {
        dayCount--;
        double result = Double.NaN;
        for (int i = 0, len = data.size(); i < len; i++) {
            if (i < dayCount) {
                result = Double.NaN;
                continue;
            }
            double sum = 0;
            for (int j = 0; j < (dayCount + 1); j++) {
                sum += data.get(i - j).getClose();
            }
            result = (+(sum / (dayCount + 1)));
        }
        return result;
    }

    public static List<HisData> calculateAll(List<HisData> list) {

        long timeS = System.currentTimeMillis();

        double sumMa5 = 0;
        double sumMa10 = 0;
        double sumMa20 = 0;

        double ema7 = 0;
        double ema30 = 0;

        double sumVMa5 = 0;
        double sumVMa10 = 0;

        double eMA12 = 0;
        double eMA26 = 0;

        double dIF = 0;
        double dEA = 0;

        double kdjK = 0;
        double kdjD = 0;
        double kdjJ = 0;

        for (int i = 0; i < list.size(); i++) {

            HisData hisData = list.get(i);

            double close = hisData.getClose();
            double vol = hisData.getVol();
//----------SMA

            double ma20 = 0;
            if (i < mMA5Index - 1) {
                hisData.setMa5(Double.NaN);
                sumMa5 = sumMa5 + close;
            } else {
                if (i > mMA5Index - 1) sumMa5 = sumMa5 - list.get(i - mMA5Index).getClose();
                sumMa5 = sumMa5 + close;
                hisData.setMa5(sumMa5 / mMA5Index);
            }

            if (i < mMA10Index - 1) {
                hisData.setMa10(Double.NaN);
                sumMa10 = sumMa10 + close;
            } else {
                if (i > mMA10Index - 1) sumMa10 = sumMa10 - list.get(i - mMA10Index).getClose();
                sumMa10 = sumMa10 + close;
                hisData.setMa10(sumMa10 / mMA10Index);
            }
            if (i < mMA20Index - 1) {
                hisData.setMa20(Double.NaN);
                sumMa20 = sumMa20 + close;
            } else {
                if (i > mMA20Index - 1) sumMa20 = sumMa20 - list.get(i - mMA20Index).getClose();
                sumMa20 = sumMa20 + close;
                ma20 = sumMa20 / mMA20Index;
                hisData.setMa20(ma20);
            }
//----------EMA
            if (i == 0) {
                ema7 = close;
                ema30 = close;
            } else {
                ema7 = ((emayz7 * close) + ((1 - emayz7) * ema7));
                ema30 = ((emayz30 * close) + ((1 - emayz30) * ema30));
            }
            hisData.setEma7(ema7);
            hisData.setEma30(ema30);
//----------BOLL

            if (i < mBOLL - 1) {
                hisData.setMBs(Double.NaN);
                hisData.setUPs(Double.NaN);
                hisData.setDNs(Double.NaN);
            } else {
                double mb = ma20;
                double md = 0;
                double sumBollc = 0;

                for (int j = i - mBOLL + 1; j < i + 1; j++) {
                    double close2 = list.get(j).getClose();
                    sumBollc += ((close2 - ma20) * (close2 - ma20));
                }
                md = Math.sqrt(sumBollc / mBOLL);
                double up = mb + (2 * md);
                double dn = mb - (2 * md);

                hisData.setMBs(mb);
                hisData.setUPs(up);
                hisData.setDNs(dn);
            }
//----------vma 5 10
            if (i < mMA5Index - 1) {
                hisData.setVma5(Double.NaN);
                sumVMa5 = sumVMa5 + vol;
            } else {
                if (i > mMA5Index - 1) sumVMa5 = sumVMa5 - list.get(i - mMA5Index).getVol();
                sumVMa5 = sumVMa5 + vol;
                hisData.setVma5(sumVMa5 / mMA5Index);
            }

            if (i < mMA10Index - 1) {
                hisData.setVma10(Double.NaN);
                sumVMa10 = sumVMa10 + vol;
            } else {
                if (i > mMA10Index - 1) sumVMa10 = sumVMa10 - list.get(i - mMA10Index).getVol();
                sumVMa10 = sumVMa10 + vol;
                hisData.setVma10(sumVMa10 / mMA10Index);
            }

//----------macd
            double mACD = 0;
            if (i == 0) {
                eMA12 = close;
                eMA26 = close;
            } else {
                eMA12 = eMA12 * 11 / 13 + close * 2 / 13;
                eMA26 = eMA26 * 25 / 27 + close * 2 / 27;
            }
            dIF = eMA12 - eMA26;
            dEA = dEA * 8 / 10 + dIF * 2 / 10;
            mACD = (dIF - dEA) * 2;
            hisData.setDea(dEA);
            hisData.setDif(dIF);
            hisData.setMacd(mACD);
//----------kdj

            if (i < mKDJ - 1) {
                hisData.setK(Double.NaN);
                hisData.setD(Double.NaN);
                hisData.setJ(Double.NaN);
            } else {
                double high = hisData.getHigh();
                double low = hisData.getLow();
                double rs = 0.0;
                for (int j = i - mKDJ + 1; j < i + 1; j++) {
                    HisData data = list.get(j);
                    high = high > data.getHigh() ? high : data.getHigh();
                    low = low < data.getLow() ? low : data.getLow();
                }
                if (high != low) {
                    rs = (close - low) / (high - low) * 100;
                } else {
                    rs = 0.0;
                }

                kdjK = (kdjK * 2 + rs) / 3;
                kdjD = (kdjD * 2 + kdjK) / 3;
                kdjJ = 3 * kdjK - 2 * kdjD;

                hisData.setK(kdjK);
                hisData.setD(kdjD);
                hisData.setJ(kdjJ);
            }
//----------Rsi

            if (i < mRSI6 - 1) {
                hisData.setRsiData6(Double.NaN);
            } else {
                double closeT = 0;
                double closeY = 0;
                double sum = 0;
                double dif = 0;

                for (int j = i - mRSI6 + 1; j < i + 1; j++) {
                    if (j < 1) continue;
                    closeT = list.get(j).getClose();
                    closeY = list.get(j - 1).getClose();

                    double c = closeT - closeY;
                    if (c > 0) {
                        sum = sum + c;
                    } else {
                        dif = dif + c;
                    }
                }
                dif = Math.abs(dif);

                double h = sum + dif;
                double rsi = sum / h * 100;
                hisData.setRsiData6(rsi);
            }

            if (i < mRSI12 - 1) {
                hisData.setRsiData12(Double.NaN);
            } else {
                double closeT = 0;
                double closeY = 0;
                double sum = 0;
                double dif = 0;

                for (int j = i - mRSI12 + 1; j < i + 1; j++) {
                    if (j < 1) continue;
                    closeT = list.get(j).getClose();
                    closeY = list.get(j - 1).getClose();

                    double c = closeT - closeY;
                    if (c > 0) {
                        sum = sum + c;
                    } else {
                        dif = dif + c;
                    }
                }
                dif = Math.abs(dif);

                double h = sum + dif;
                double rsi = sum / h * 100;
                hisData.setRsiData12(rsi);
            }

            if (i < mRSI24 - 1) {
                hisData.setRsiData24(Double.NaN);
            } else {
                double closeT = 0;
                double closeY = 0;
                double sum = 0;
                double dif = 0;

                for (int j = i - mRSI24 + 1; j < i + 1; j++) {
                    if (j < 1) continue;
                    closeT = list.get(j).getClose();
                    closeY = list.get(j - 1).getClose();

                    double c = closeT - closeY;
                    if (c > 0) {
                        sum = sum + c;
                    } else {
                        dif = dif + c;
                    }
                }
                dif = Math.abs(dif);

                double h = sum + dif;
                double rsi = sum / h * 100;
                hisData.setRsiData24(rsi);
            }

        }
        long timeE = System.currentTimeMillis() - timeS;
        Log.i("1234", "current time ：" + timeE);
        return list;
    }

    public static List<HisData> calculateAllByBigDecimal(List<HisData> list) {

        long timeS = System.currentTimeMillis();

        double sumMa5 = 0;
        double sumMa10 = 0;
        double sumMa20 = 0;

        double ema7 = 0;
        double ema30 = 0;

        double sumVMa5 = 0;
        double sumVMa10 = 0;

        double eMA12 = 0;
        double eMA26 = 0;

        double dIF = 0;
        double dEA = 0;

        double kdjK = 50.0;
        double kdjD = 50.0;
        double kdjJ = 0;

        double macd2_13 = 0;
        double macd11_13 = 0;
        double macd2_27 = 0;
        double macd25_27 = 0;


        for (int i = 0; i < list.size(); i++) {

            HisData hisData = list.get(i);

            double close = hisData.getClose();
            double vol = hisData.getVol();
//----------SMA

            double ma20 = 0;
            if (i < mMA5Index - 1) {
                hisData.setMa5(Double.NaN);
                sumMa5 = DoubleArith.add(sumMa5, close);
            } else {
                if (i > mMA5Index - 1)
                    sumMa5 = DoubleArith.sub(sumMa5, list.get(i - mMA5Index).getClose());
                sumMa5 = DoubleArith.add(sumMa5, close);
                hisData.setMa5(DoubleArith.div(sumMa5, (double) mMA5Index));
            }


            if (i < mMA10Index - 1) {
                hisData.setMa10(Double.NaN);
                sumMa10 = DoubleArith.add(sumMa10, close);
            } else {
                if (i > mMA10Index - 1)
                    sumMa10 = DoubleArith.sub(sumMa10, list.get(i - mMA10Index).getClose());
                sumMa10 = DoubleArith.add(sumMa10, close);
                hisData.setMa10(DoubleArith.div(sumMa10, (double) mMA10Index));
            }
            if (i < mMA20Index - 1) {
                hisData.setMa20(Double.NaN);
                sumMa20 = DoubleArith.add(sumMa20, close);
            } else {
                if (i > mMA20Index - 1)
                    sumMa20 = DoubleArith.sub(sumMa20, list.get(i - mMA20Index).getClose());
                sumMa20 = DoubleArith.add(sumMa20, close);
                ma20 = DoubleArith.div(sumMa20, (double) mMA20Index);
                hisData.setMa20(ma20);
            }
//----------EMA
            if (i == 0) {
                ema7 = close;
                ema30 = close;
                emayz7 = DoubleArith.div(2d, 8d);
                emayz7_ = DoubleArith.div(6d, 8d);
                emayz30 = DoubleArith.div(2d, 31d);
                emayz30_ = DoubleArith.div(29d, 31d);
            } else {
                ema7 = DoubleArith.add(DoubleArith.mul(emayz7, close), DoubleArith.mul(emayz7_, ema7));
                ema30 = DoubleArith.add(DoubleArith.mul(emayz30, close), DoubleArith.mul(emayz30_, ema30));
            }
            hisData.setEma7(ema7);
            hisData.setEma30(ema30);
//----------BOLL

            if (i < mBOLL - 1) {
                hisData.setMBs(Double.NaN);
                hisData.setUPs(Double.NaN);
                hisData.setDNs(Double.NaN);
            } else {
                double mb = ma20;
                double md = 0;
                double sumBollc = 0;

                for (int j = i - mBOLL + 1; j < i + 1; j++) {
                    double close2 = list.get(j).getClose();
                    double asm = DoubleArith.sub(close2, ma20);
                    sumBollc = DoubleArith.add(sumBollc, DoubleArith.mul(asm, asm));
                }
                md = Math.sqrt(DoubleArith.div(sumBollc, (double) mBOLL));
                double k = DoubleArith.mul(2d, md);
                double up = DoubleArith.add(mb, k);
                double dn = DoubleArith.sub(mb, k);

                hisData.setMBs(mb);
                hisData.setUPs(up);
                hisData.setDNs(dn);
            }
//----------vma 5 10
            if (i < mMA5Index - 1) {
                hisData.setVma5(Double.NaN);
                sumVMa5 = DoubleArith.add(sumVMa5, vol);
            } else {
                if (i > mMA5Index - 1)
                    sumVMa5 = DoubleArith.sub(sumVMa5, list.get(i - mMA5Index).getVol());
                sumVMa5 = DoubleArith.add(sumVMa5, vol);
                hisData.setVma5(DoubleArith.div(sumVMa5, (double) mMA5Index));
            }

            if (i < mMA10Index - 1) {
                hisData.setVma10(Double.NaN);
                sumVMa10 = DoubleArith.add(sumVMa10, vol);
            } else {
                if (i > mMA10Index - 1)
                    sumVMa10 = DoubleArith.sub(sumVMa10, list.get(i - mMA10Index).getVol());
                sumVMa10 = DoubleArith.add(sumVMa10, vol);
                hisData.setVma10(DoubleArith.div(sumVMa10, (double) mMA10Index));
            }

//----------macd
            double mACD = 0;
            if (i == 0) {
                macd11_13 = DoubleArith.div(11d, 13d);
                macd2_13 = DoubleArith.div(2d, 13d);
                macd2_27 = DoubleArith.div(2d, 27d);
                macd25_27 = DoubleArith.div(25d, 27d);
                eMA12 = close;
                eMA26 = close;
            } else {
                eMA12 = DoubleArith.add(DoubleArith.mul(eMA12, macd11_13), DoubleArith.mul(close, macd2_13));
                eMA26 = DoubleArith.add(DoubleArith.mul(eMA26, macd25_27), DoubleArith.mul(close, macd2_27));
            }
            dIF = DoubleArith.sub(eMA12, eMA26);
            dEA = DoubleArith.add(DoubleArith.mul(dEA, 0.8d), DoubleArith.mul(dIF, 0.2d));
            mACD = DoubleArith.mul(DoubleArith.sub(dIF, dEA), 2d);
            hisData.setDea(dEA);
            hisData.setDif(dIF);
            hisData.setMacd(mACD);
//----------kdj

            if (i < mKDJ - 1) {
                hisData.setK(Double.NaN);
                hisData.setD(Double.NaN);
                hisData.setJ(Double.NaN);
            } else {
                double high = hisData.getHigh();
                double low = hisData.getLow();
                double rs = 0.0;
                for (int k0 = i - mKDJ + 1; k0 < i + 1; k0++) {
                    HisData data = list.get(k0);
                    high = high > data.getHigh() ? high : data.getHigh();
                    low = low < data.getLow() ? low : data.getLow();
                }
                if (high != low) {
                    rs = DoubleArith.mul(DoubleArith.div(DoubleArith.sub(close, low), DoubleArith.sub(high, low)), 100d);
                } else {
                    rs = 0.0;
                }

                kdjK = DoubleArith.div(DoubleArith.add(DoubleArith.mul(kdjK, 2d), rs), 3d);
                kdjD = DoubleArith.div(DoubleArith.add(DoubleArith.mul(kdjD, 2d), kdjK), 3d);
                kdjJ = DoubleArith.sub(DoubleArith.mul(kdjK, 3d), DoubleArith.mul(kdjD, 2d));

                hisData.setK(kdjK);
                hisData.setD(kdjD);
                hisData.setJ(kdjJ);
            }
//----------Rsi

            if (i < mRSI6 - 1) {
                hisData.setRsiData6(Double.NaN);
            } else {
                double closeT = 0;
                double closeY = 0;
                double sum = 0;
                double dif = 0;

                for (int j = i - mRSI6 + 1; j < i + 1; j++) {
                    if (j < 1) continue;
                    closeT = list.get(j).getClose();
                    closeY = list.get(j - 1).getClose();
                    double c = DoubleArith.sub(closeT, closeY);
                    if (c > 0) {
                        sum = DoubleArith.add(sum, c);
                    } else {
                        dif = DoubleArith.add(dif, c);
                    }
                }
                dif = Math.abs(dif);

                double h = DoubleArith.add(sum, dif);
                double f = 0;
                try {
                    f = DoubleArith.div(sum, h);
                } catch (Exception e) {
                    f = 0;
                }
                double rsi = DoubleArith.mul(f, 100d);
                hisData.setRsiData6(rsi);
            }

            if (i < mRSI12 - 1) {
                hisData.setRsiData12(Double.NaN);
            } else {
                double closeT = 0;
                double closeY = 0;
                double sum = 0;
                double dif = 0;

                for (int j = i - mRSI12 + 1; j < i + 1; j++) {
                    if (j < 1) continue;
                    closeT = list.get(j).getClose();
                    closeY = list.get(j - 1).getClose();
                    double c = DoubleArith.sub(closeT, closeY);
                    if (c > 0) {
                        sum = DoubleArith.add(sum, c);
                    } else {
                        dif = DoubleArith.add(dif, c);
                    }
                }
                dif = Math.abs(dif);

                double h = DoubleArith.add(sum, dif);
                double f = 0;
                try {
                    f = DoubleArith.div(sum, h);
                } catch (Exception e) {
                    f = 0;
                }
                double rsi = DoubleArith.mul(f, 100d);
                hisData.setRsiData12(rsi);
            }

            if (i < mRSI24 - 1) {
                hisData.setRsiData24(Double.NaN);
            } else {
                double closeT = 0;
                double closeY = 0;
                double sum = 0;
                double dif = 0;

                for (int j = i - mRSI24 + 1; j < i + 1; j++) {
                    if (j < 1) continue;
                    closeT = list.get(j).getClose();
                    closeY = list.get(j - 1).getClose();
                    double c = DoubleArith.sub(closeT, closeY);
                    if (c > 0) {
                        sum = DoubleArith.add(sum, c);
                    } else {
                        dif = DoubleArith.add(dif, c);
                    }
                }
                dif = Math.abs(dif);

                double h = DoubleArith.add(sum, dif);
                double f = 0;
                try {
                    f = DoubleArith.div(sum, h);
                } catch (Exception e) {
                    f = 0;
                }
                double rsi = DoubleArith.mul(f, 100d);
                hisData.setRsiData24(rsi);
            }

        }
        long timeE = System.currentTimeMillis() - timeS;
        Log.i("1234", "current time ：" + timeE);
        return list;
    }


}

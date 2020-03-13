package com.guoziwei.klinelib.util;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by hou on 2015/8/14.
 */
public class DoubleUtil {

    public static double parseDouble(String parserDouble) {
        try {
            return Double.parseDouble(parserDouble);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String format2Decimal(Double d) {
        NumberFormat instance = DecimalFormat.getInstance();
        instance.setMinimumFractionDigits(2);
        instance.setMaximumFractionDigits(2);
        return instance.format(d);
    }

    public static String formatDecimal(Double d) {
        NumberFormat instance = DecimalFormat.getInstance();
        instance.setMinimumFractionDigits(0);
        instance.setMaximumFractionDigits(8);
        return instance.format(d).replace(",", "");
    }


    /**
     * converting a double number to string by digits
     */
    public static String getStringByDigits(double num, int digits) {

        if (Double.isNaN(num)) num = 0;
        return new BigDecimal(num + "")
                //.setScale(digits, BigDecimal.ROUND_HALF_DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * converting a double number to string by digits
     */
    public static String getStringByDigits(float num, int digits) {

        if (Float.isNaN(num)) num = 0;
        return new BigDecimal(num + "")
                //.setScale(digits, BigDecimal.ROUND_HALF_DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }

}

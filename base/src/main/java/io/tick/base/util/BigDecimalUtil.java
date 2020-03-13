package io.tick.base.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class BigDecimalUtil {
    //有数据的时候
    public static String defMoney = "0.01";
    public static String defNum = "0.0001";
    //没有网络 或者没有数据的时候
    public static String noNetdefMoney = "0.00";
    public static String noNetdefNum = "0.0000";

    public static boolean isGreater(String value1, String value2) {
        if (TextUtils.isEmpty(value1)) {
            value1 = "0";
        }
        if (TextUtils.isEmpty(value2)) {
            value2 = "0";
        }
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.compareTo(b2) > 0;
    }

    /**
     * 方法
     * <p>
     * 用途++
     */
    public static String add(String value1, String value2) {
        try {
            BigDecimal b1 = new BigDecimal(deletePoint(value1));
            BigDecimal b2 = new BigDecimal(deletePoint(value2));
            return b1.add(b2).toPlainString();
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String sub(String value1, String value2) {
        try {
            BigDecimal b1 = new BigDecimal(deletePoint(value1));
            BigDecimal b2 = new BigDecimal(deletePoint(value2));
            return b1.subtract(b2).toPlainString();
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 方法
     * <p>
     * 用途--
     */
    public static String sub(String value1, String value2, boolean isMoney) {
        String num = "--";
        if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
            BigDecimal b1 = new BigDecimal(deletePoint(value1));
            BigDecimal b2 = new BigDecimal(deletePoint(value2));
            int compare = b1.compareTo(b2);
            if (compare <= 0) {
                if (isMoney) {
                    num = noNetdefMoney;
                } else {
                    num = noNetdefNum;
                }
            } else {
                num = b1.subtract(b2).toPlainString();
            }
        }
        return num;
    }

    public static boolean compare(String value1, String value2) {
        if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
            try {
                BigDecimal b1 = new BigDecimal(deletePoint(value1));
                BigDecimal b2 = new BigDecimal(deletePoint(value2));
                return b1.compareTo(b2) > 0;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean equal(String value1, String value2) {
        if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
            try {
                BigDecimal b1 = new BigDecimal(deletePoint(value1));
                BigDecimal b2 = new BigDecimal(deletePoint(value2));
                return b1.compareTo(b2) == 0;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 乘法
     */
    public static String mul(String value1, String value2) {
        String v = "";
        if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
            BigDecimal b1 = new BigDecimal(deletePoint(value1));
            BigDecimal b2 = new BigDecimal(deletePoint(value2));
            v = b1.multiply(b2).toPlainString();
        }

        return v;
    }

    public static String mul(String value1, String value2, int scale) {
        String v = "";
        if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
            BigDecimal b1 = new BigDecimal(deletePoint(value1));
            BigDecimal b2 = new BigDecimal(deletePoint(value2));
            v = b1.multiply(b2).setScale(scale, BigDecimal.ROUND_DOWN).toPlainString();
        }
        return v;
    }

    /**
     * 除法
     */
    public static String div(String value1, String value2, int scale)
            throws IllegalAccessException {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        BigDecimal b1 = new BigDecimal(deletePoint(value1));
        BigDecimal b2 = new BigDecimal(deletePoint(value2));
        String v2 = b1.divide(b2, scale, BigDecimal.ROUND_DOWN).toPlainString();
        return v2;
    }

    /**
     * 交易除法  小数后两位 计算汇率
     */
    public static String divTradeRate(String value1, String value2, int scale)
            throws IllegalAccessException {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        if (TextUtils.isEmpty(value2)) {
            throw new IllegalAccessException("请传入正确的买卖税率");
        }
        double v1 = Double.parseDouble(deletePoint(value1));
        double v2 = Double.parseDouble(deletePoint(value2));
        String result = "0";
        if (v1 > 0 && v2 > 0) {
            double val2 = 1 + v2;
            BigDecimal b1 = new BigDecimal(deletePoint(value1));
            BigDecimal b2 = new BigDecimal(val2);
            String v = b1.divide(b2, scale, BigDecimal.ROUND_DOWN).toPlainString();
            result = v;
        } else {
            result = formatByDecimalValue(value1, scale);
        }
        return result;
    }

    /**
     * 交易除法  小数后两位
     */
    public static String divTrade(String value1, String value2, int scale)
            throws IllegalAccessException {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        if (TextUtils.isEmpty(value2)) {
            throw new IllegalAccessException("请传入正确的买卖税率");
        }
        double v1 = Double.parseDouble(deletePoint(value1));
        double v2 = Double.parseDouble(deletePoint(value2));
        if (v2 == 0) {
            throw new IllegalAccessException("除数不能为0");
        }
        String result;
        if (v1 > 0 && v2 > 0) {
            BigDecimal b1 = new BigDecimal(deletePoint(value1));
            BigDecimal b2 = new BigDecimal(deletePoint(value2));
            String v = b1.divide(b2, scale, BigDecimal.ROUND_DOWN).toPlainString();
            result = v;
        } else {
            String s = String.valueOf(v1);
            int i = s.indexOf(".");
            if (i > 0) {
                if (s.length() > i + scale + 1) {
                    result = s.substring(0, i + scale + 1);
                } else {
                    result = s;
                }
            } else {
                result = s;
            }
        }
        return result;
    }

    /**
     * 交易乘法
     */
    public static String mulTrade(String value1, String value2, int scale) {
        String result = "";
        if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
            BigDecimal b1 = new BigDecimal(deletePoint(value1));
            BigDecimal b2 = new BigDecimal(deletePoint(value2));
            String s = b1.multiply(b2).toPlainString();
            int i = s.indexOf(".");
            if (i > 0) {
                if (s.length() > i + scale + 1) {
                    result = s.substring(0, i + scale + 1);
                } else {
                    result = s;
                }
            } else {
                result = s;
            }
        }

        return result;
    }

    /**
     * 截取有效小数位
     * param string
     */
    public static String formatByDecimalValue(String value, int decimalValue) {
        return new BigDecimal(deletePoint(value))
                .setScale(decimalValue, BigDecimal.ROUND_DOWN)
                .toPlainString();
    }


    /**
     * 截取有效小数位
     * param double
     */
    public static String formatByDecimalValue(Double value, int decimalValue) {
        return new BigDecimal(value)
                .setScale(decimalValue, BigDecimal.ROUND_DOWN)
                .toPlainString();
    }

    public static String formatByDecimalValue(String value) {
        return new BigDecimal(deletePoint(value))
                .setScale(8, BigDecimal.ROUND_DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 删除一个数字，最后的一位小数点45550.==45550
     */
    public static String deletePoint(String value) {
        if (value.endsWith(".")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }

    public static String stripTrailingZeros(String value) {
        BigDecimal b = new BigDecimal(value);
        if (b.compareTo(new BigDecimal("0")) == 0) {
            return "0";
        } else {
            return b.stripTrailingZeros().toPlainString();
        }
    }

    public static BigDecimal[] divideAndRemainder(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(deletePoint(value1));
        BigDecimal b2 = new BigDecimal(deletePoint(value2));
        BigDecimal[] results = b1.divideAndRemainder(b2);
        return results;
    }

    public static boolean hasRemainder(String value1, String value2) {
        BigDecimal[] ret = divideAndRemainder(value1, value2);
        return new BigDecimal("0").compareTo(ret[1]) == 0;
    }

    public static String getPercentString(String input, int scale) {
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(scale);
        return nt.format(Double.valueOf(input));
    }
}

package io.alf.exchange.util;

import android.text.TextUtils;

import androidx.annotation.ColorInt;

import java.math.BigDecimal;
import java.util.List;

import io.alf.exchange.App;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.ExchangeRateBean;
import io.alf.exchange.mvp.bean.TotalAssetBean;
import io.tick.base.util.BigDecimalUtil;


public class PriceConvertUtil {
    public static String convert(String price) {
        String resultPrice;
        double originalPrice;
        try {
            originalPrice = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            originalPrice = 0;
            e.printStackTrace();
        }
        if (originalPrice == 0) {                // price == 0
            resultPrice = "0.00";
        } else if (originalPrice < 0.00000001) { //  price < 0.00000001
            resultPrice = "<0.00000001";
        } else if (originalPrice < 0.0001) {    // 0.00000001 <= price < 0.0001
            resultPrice = formartByScale(price, 8);
        } else if (originalPrice < 0.001) {     // 0.0001 <= price < 0.001
            resultPrice = formartByScale(price, 7);
        } else if (originalPrice < 0.01) {      // 0.001 <= price < 0.01
            resultPrice = formartByScale(price, 6);
        } else if (originalPrice < 0.1) {      //  0.01 <= price < 0.1
            resultPrice = formartByScale(price, 5);
        } else if (originalPrice < 10) {       //  0.1 <= price < 10
            resultPrice = formartByScale(price, 4);
        } else if (originalPrice >= 10) {     // price >= 10
            resultPrice = formartByScale(price, 2);
        } else {                              // price < 0
            resultPrice = "0.00";
        }
        return resultPrice;
    }

    private static String formartByScale(String price, int scale) {
        return new BigDecimal(price)
                .setScale(scale, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public static ExchangeRateBean.ExchangeRateListBean getExchangeBean() {
        ExchangeRateBean bean = CexDataPersistenceUtils.getAllLegalCurrencyInfo();
        ExchangeRateBean.ExchangeRateListBean currencyLegalCurrency =
                CexDataPersistenceUtils.getCurrentLegalCurrencyInfo();
        if (bean == null) {
            return currencyLegalCurrency;
        } else {
            if (currencyLegalCurrency != null) {
                for (ExchangeRateBean.ExchangeRateListBean b : bean.exchangeRateList) {
                    if (TextUtils.equals(b.currency, currencyLegalCurrency.currency)) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    public static String convertAmountWithRate(String amount) {
        ExchangeRateBean.ExchangeRateListBean bean = getExchangeBean();
        if (bean != null) {
            return BigDecimalUtil.mul(amount, bean.exchangeRate, 2);
        } else {
            return amount;
        }
    }

    public static String getUsdtPrice(String currencyAlias) {
        TotalAssetBean bean = CexDataPersistenceUtils.getTotalAssetBean();
        if (bean != null) {
            List<TotalAssetBean.CurrencyAssetBean> currencyList = bean.currencyList;
            if (currencyList != null && currencyList.size() > 0) {
                for (TotalAssetBean.CurrencyAssetBean currencyAssetBean : currencyList) {
                    if (StringUtils.equals(currencyAssetBean.currency, currencyAlias)) {
                        return currencyAssetBean.usdtPrice;
                    }
                }
            }
        }
        return "";
    }

    public static String getUsdtAmount(String currencyAlias, String amount) {
        String price;
        if (StringUtils.equalsIgnoreCase(currencyAlias, "eth")) {
            price = CexDataPersistenceUtils.getUsdtPrice("eth", "eth");
        } else {
            price = getUsdtPrice(currencyAlias);
        }
        if (!StringUtils.isEmpty(price)) {
            String usdtAmount = BigDecimalUtil.mul(price, amount);
            return String.format("%s USDT", BigDecimalUtil.formatByDecimalValue(usdtAmount, 4));
        }
        return "0.00 USDT";
    }

    public static String getUsdtAmount(String usdtAmount) {
        return String.format("%s USDT", BigDecimalUtil.formatByDecimalValue(usdtAmount, 4));
    }

    public static String getLegalCurrencyAmount(String usdAmount) {
        if (!StringUtils.isEmpty(usdAmount)) {
            ExchangeRateBean.ExchangeRateListBean bean = getExchangeBean();
            if (bean != null) {
                return String.format("%s%s", bean.currencySymbol,
                        PriceConvertUtil.convertAmountWithRate(usdAmount));
            } else {
                return String.format("$%s", BigDecimalUtil.formatByDecimalValue(usdAmount, 2));
            }
        } else {
            return String.format("$--");
        }
    }

    public static String getWavePrice(String direction, String wavePrice) {
        String ret = wavePrice;
        switch (direction) { //  涨跌价格
            case "-1": {
                ret = "-" + wavePrice;
                break;
            }
            case "0": {
                ret = wavePrice;
                break;
            }
            case "1": {
                ret = "+" + wavePrice;
                break;
            }
        }
        return ret;
    }

    public static String getWavePercent(String direction, String percent) {
        String ret = BigDecimalUtil.formatByDecimalValue(percent, 2) + "%";
        switch (direction) { //  涨跌价格
            case "-1": {
                ret = "-" + BigDecimalUtil.formatByDecimalValue(percent, 2) + "%";
                break;
            }
            case "0": {
                ret = BigDecimalUtil.formatByDecimalValue(percent, 2) + "%";
                break;
            }
            case "1": {
                ret = "+" + BigDecimalUtil.formatByDecimalValue(percent, 2) + "%";
                break;
            }
        }
        return ret;
    }

    @ColorInt
    public static int getTextColor(String direction) {
        int ret = App.getInstance().getResources().getColor(R.color.textPrimary);
        switch (direction) { //  涨跌价格
            case "-1":
                ret = App.getInstance().getResources().getColor(R.color.color_decrease);
                break;
            case "0":
                ret = App.getInstance().getResources().getColor(R.color.textPrimary);
                break;
            case "1":
                ret = App.getInstance().getResources().getColor(R.color.color_increase);
                break;
        }
        return ret;
    }


}

package io.alf.exchange.util;

import android.text.TextUtils;

import io.cex.mqtt.bean.OrderBookBean;


public class FilterOrderBookUtil {

    public static OrderBookBean filterOrderBook(String symbol, String depthCode,
            OrderBookBean orderBookBean) {
        if (orderBookBean != null && TextUtils.equals(symbol, orderBookBean.symbol)
                && TextUtils.equals(depthCode, orderBookBean.step)) {
            return orderBookBean;
        } else {
            return null;
        }
    }
}

package io.alf.exchange.mvp.bean;

import java.io.Serializable;
import java.util.List;

public class ExchangeRateBean implements Serializable {
    public DefaultCurrencyBean defaultCurrency;
    public List<ExchangeRateListBean> exchangeRateList;

    public static class DefaultCurrencyBean {
        public String currency;
        public String currencyName;
        public String currencySymbol;
        public String exchangeRate;
    }

    public static class ExchangeRateListBean {
        public String currency;//CNY
        public String currencyName;//人民币
        public String currencySymbol;//¥
        public String exchangeRate;//6。87xxx
        public boolean checked;
    }
}

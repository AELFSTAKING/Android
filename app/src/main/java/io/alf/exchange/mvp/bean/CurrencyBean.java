package io.alf.exchange.mvp.bean;

import java.io.Serializable;
import java.util.List;

public class CurrencyBean {

    public List<ListBean> list;

    public static class ListBean implements Serializable {
        public String alias;
        public String chain;
        public String convertRate;
        public String currency;
        public String icon128;
        public String icon32;
        public Object maxAmount;
        public Object minAmount;
        public String scale;
        public String status;
        public String tokenChain;
        public String tokenCurrency;
        public long utcCreate;
        public long utcUpdate;
    }
}

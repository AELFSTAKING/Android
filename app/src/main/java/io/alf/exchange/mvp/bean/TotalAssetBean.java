package io.alf.exchange.mvp.bean;

import java.io.Serializable;
import java.util.List;

public class TotalAssetBean implements Serializable {

    public String totalUsdAsset;
    public String totalUsdtAsset;
    public List<CurrencyAssetBean> currencyList;

    public static class CurrencyAssetBean implements Serializable {
        public String balance;
        public String currency;
        public String targetCurrency;
        public String usdtAsset;
        public String usdtPrice;
    }
}

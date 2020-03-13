package io.alf.exchange.mvp.bean.local;

public class AssetBean {
    public String imgUrl;
    public String currency;
    public String chain;
    public String balance;

    public AssetBean(String imgUrl, String currency, String chain, String balance) {
        this.imgUrl = imgUrl;
        this.currency = currency;
        this.chain = chain;
        this.balance = balance;
    }
}

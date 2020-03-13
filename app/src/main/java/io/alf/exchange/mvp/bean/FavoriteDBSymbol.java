package io.alf.exchange.mvp.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by xiaojianjun on 2018/6/15.
 * 本地数据库存储自选交易对
 */
@Entity
public class FavoriteDBSymbol {
    @Id
    private long id;
    private String symbol;

    public FavoriteDBSymbol() {
    }

    public FavoriteDBSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}

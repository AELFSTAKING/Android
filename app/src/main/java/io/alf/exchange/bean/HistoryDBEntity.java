package io.alf.exchange.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Administrator on 2018/4/8.
 *
 */
@Entity
public class HistoryDBEntity {

    @Id
    private long id;

    private String symbol;
    private String price;
    private String max;
    private String min;
    private String quantity;
    private String applies;
    private String usdPrice;
    private String wavePrice;
    private String wavePercent;
    private String direction;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getApplies() {
        return applies;
    }

    public void setApplies(String applies) {
        this.applies = applies;
    }

    public String getUsdPrice() {
        return usdPrice;
    }

    public void setUsdPrice(String usdPrice) {
        this.usdPrice = usdPrice;
    }

    public String getWavePrice() {
        return wavePrice;
    }

    public void setWavePrice(String wavePrice) {
        this.wavePrice = wavePrice;
    }

    public String getWavePercent() {
        return wavePercent;
    }

    public void setWavePercent(String wavePercent) {
        this.wavePercent = wavePercent;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}

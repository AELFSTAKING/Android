package io.alf.exchange.bean;

public class TradeEvent {
    public static final String BUY = "buy";
    public static final String SELL = "sell";
    public String action;
    public String symbol;

    public TradeEvent(String action, String symbol) {
        this.action = action;
        this.symbol = symbol;
    }

    public static TradeEvent newBuyEvent(String symbol) {
        return new TradeEvent(BUY, symbol);
    }

    public static TradeEvent newSellEvent(String symbol) {
        return new TradeEvent(SELL, symbol);
    }
}

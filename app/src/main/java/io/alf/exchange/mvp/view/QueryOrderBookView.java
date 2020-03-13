package io.alf.exchange.mvp.view;


import io.cex.mqtt.bean.OrderBookBean;
import io.tick.base.mvp.IView;

public interface QueryOrderBookView extends IView {
    void onQueryOrderBook(OrderBookBean orderBookBean);
}

package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.TradeSymbolInfoBean;
import io.tick.base.mvp.IView;

public interface QuerySymbolTradeInfoView extends IView {
    void onQueryTradeSymbolInfo(boolean success, TradeSymbolInfoBean data);
}

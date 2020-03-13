package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.CurrencyBean;
import io.tick.base.mvp.IView;

public interface QueryCurrencyListView extends IView {
    void onQueryCurrencyList(CurrencyBean data);
}

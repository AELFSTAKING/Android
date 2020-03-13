package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.CurrencyInfoBean;
import io.tick.base.mvp.IView;

public interface QueryCurrencyInfoView extends IView {

    void onQueryCurrencyInfo(boolean success, CurrencyInfoBean data);
}

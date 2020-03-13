package io.alf.exchange.mvp.view;

import io.tick.base.mvp.IView;

public interface QueryPriceView extends IView {
    void onQueryUsdtPrice(String currency, String price);
}

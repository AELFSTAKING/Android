package io.alf.exchange.mvp.view;

import io.tick.base.mvp.IView;

public interface QueryBalanceView extends IView {
    void onQueryBalance(String chain, String currency, String amount);
}

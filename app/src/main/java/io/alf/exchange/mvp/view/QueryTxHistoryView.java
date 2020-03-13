package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.TxHistoryBean;
import io.tick.base.mvp.IView;

public interface QueryTxHistoryView extends IView {
    void onQueryTxHistory(TxHistoryBean data);
}

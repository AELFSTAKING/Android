package io.alf.exchange.mvp.view;

import io.alf.exchange.mvp.bean.TxInfoBean;
import io.tick.base.mvp.IView;

public interface TxRecordDetailView extends IView {
    void onQueryTxInfo(boolean success, TxInfoBean data);
}

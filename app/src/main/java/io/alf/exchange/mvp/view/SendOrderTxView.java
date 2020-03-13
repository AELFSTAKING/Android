package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.SendTxBean;
import io.tick.base.mvp.IView;

public interface SendOrderTxView extends IView {

    void onSendOrderTx(String orderId, SendTxBean data);
}

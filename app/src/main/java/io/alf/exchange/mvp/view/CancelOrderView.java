package io.alf.exchange.mvp.view;

import io.alf.exchange.mvp.bean.CancelOrderBean;
import io.alf.exchange.mvp.bean.SendTxBean;
import io.tick.base.mvp.IView;

public interface CancelOrderView extends IView {

    void onCancelUserOrder(boolean success, CancelOrderBean data, String password);

    void onCancelCallback(String orderNo, boolean success);

    void onSendCancelOrderTx(String orderId, SendTxBean data);
}

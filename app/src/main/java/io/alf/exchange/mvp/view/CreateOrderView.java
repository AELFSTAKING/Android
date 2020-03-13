package io.alf.exchange.mvp.view;

import io.alf.exchange.mvp.bean.CreateOrderBean;
import io.alf.exchange.mvp.bean.SendTxBean;
import io.tick.base.mvp.IView;

public interface CreateOrderView extends IView {
    void onCreateUserOrder(String password, CreateOrderBean orderBean);

    void onSendOrderTx(String orderId, SendTxBean data);

    void onPayCallback(String orderId, boolean success);
}

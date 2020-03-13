package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.BindAddressListBean;
import io.tick.base.mvp.IView;

public interface DeleteBindAddressView extends IView {
    void onDeleteBindAddress(boolean success, BindAddressListBean data);
}

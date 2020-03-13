package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.BindAddressListBean;
import io.tick.base.mvp.IView;

public interface QueryBindAddressView extends IView {
    void onQueryBindAddress(int action, BindAddressListBean data);
}

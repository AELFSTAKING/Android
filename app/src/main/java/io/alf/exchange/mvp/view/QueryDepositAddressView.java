package io.alf.exchange.mvp.view;

import io.alf.exchange.mvp.bean.DepositAddressBean;
import io.tick.base.mvp.IView;

public interface QueryDepositAddressView extends IView {
    void onQueryDepositAddress(String currency, DepositAddressBean data);
}

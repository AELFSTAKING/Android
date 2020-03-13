package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.FeeBean;
import io.tick.base.mvp.IView;

public interface QueryFeeView extends IView {
    void onQueryFee(FeeBean data);
}

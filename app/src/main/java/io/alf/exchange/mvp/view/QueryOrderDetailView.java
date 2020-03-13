package io.alf.exchange.mvp.view;

import io.alf.exchange.mvp.bean.OrderDetailBean;
import io.tick.base.mvp.IView;

public interface QueryOrderDetailView extends IView {
    void onQueryOrderDetail(OrderDetailBean data);
}

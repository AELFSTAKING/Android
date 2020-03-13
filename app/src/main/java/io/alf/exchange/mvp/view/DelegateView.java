package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.OrderHistoryBean;
import io.tick.base.mvp.IView;

public interface DelegateView extends IView {
    void onQueryCurrentOrderList(boolean success, OrderHistoryBean data);

    void onQueryHistoryOrderList(boolean success, OrderHistoryBean data);
}

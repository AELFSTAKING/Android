package io.alf.exchange.mvp.view;

import java.util.List;

import io.cex.mqtt.bean.DealBean;
import io.tick.base.mvp.IView;

public interface QueryDealListView extends IView {

    void onQueryDealList(String symbol, List<DealBean> dealBeanList);
}

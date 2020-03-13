package io.alf.exchange.mvp.view;


import io.cex.mqtt.bean.DepthBean;
import io.tick.base.mvp.IView;

public interface QueryDepthDataView extends IView {
    void onQueryDepthData(DepthBean depthBean);
}

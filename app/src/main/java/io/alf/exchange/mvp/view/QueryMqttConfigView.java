package io.alf.exchange.mvp.view;

import io.alf.exchange.mvp.bean.MqttConfigBean;
import io.tick.base.mvp.IView;

public interface QueryMqttConfigView extends IView {
    void onQueryMqttConfig(MqttConfigBean bean);
}

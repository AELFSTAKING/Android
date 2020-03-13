package io.alf.exchange.mvp.presenter;


import com.lzy.okgo.model.Response;

import io.alf.exchange.mvp.bean.MqttConfigBean;
import io.alf.exchange.mvp.view.QueryMqttConfigView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryMqttConfigPresenter extends BasePresenter<QueryMqttConfigView> {

    public void queryMqttConfig() {
        HttpUtils.postRequest(BaseUrl.QUERY_MQTT_INFO, getView(), generateRequestHeader(), generateRequestBody(),
                new BaseCallback<ResponseBean<MqttConfigBean>>(BaseUrl.QUERY_MQTT_INFO, getView(), false) {
                    @Override
                    public void onSuccess(Response<ResponseBean<MqttConfigBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryMqttConfig(response.body().data);
                        }
                    }
                });
    }
}

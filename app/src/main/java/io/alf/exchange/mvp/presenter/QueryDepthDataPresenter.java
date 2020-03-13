package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.view.QueryDepthDataView;
import io.cex.mqtt.bean.DepthBean;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryDepthDataPresenter extends BasePresenter<QueryDepthDataView> {

    public void queryDepthData(String symbol) {
        Map<String, Object> data = new HashMap<>();
        data.put("symbol", symbol);
        HttpUtils.postRequest(BaseUrl.QUERY_DEPTH_DATA, getView(), generateRequestHeader(), data,
                new BaseCallback<ResponseBean<DepthBean>>(BaseUrl.QUERY_DEPTH_DATA, getView(), false) {
                    @Override
                    public void onSuccess(Response<ResponseBean<DepthBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryDepthData(response.body().data);
                        }
                    }
                });
    }
}

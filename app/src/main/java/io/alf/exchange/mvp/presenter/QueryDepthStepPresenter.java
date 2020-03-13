package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.alf.exchange.mvp.view.QueryDepthStepView;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class QueryDepthStepPresenter extends BasePresenter<QueryDepthStepView> {

    public void queryDepthStep(String symbol) {
        Map<String, Object> data = new HashMap<>();
        data.put("symbol", symbol);
        HttpUtils.postRequest(BaseUrl.QUERY_DEPTH_STEP, getView(), generateRequestHeader(), data,
                new BaseCallback<ResponseBean<List<String>>>(BaseUrl.QUERY_DEPTH_STEP, getView(), false) {
                    @Override
                    public void onSuccess(Response<ResponseBean<List<String>>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            Collections.reverse(response.body().data);
                            CexDataPersistenceUtils.putDepthStepList(symbol, response.body().data);
                            getView().onQueryDepthStep(response.body().data);
                        }
                    }
                });
    }
}

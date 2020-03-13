package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.alf.exchange.mvp.view.QueryDealListView;
import io.cex.mqtt.bean.DealBean;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryDealListPresenter extends BasePresenter<QueryDealListView> {

    public void queryDealList(String symbol) {
        Map<String, Object> data = new HashMap<>();
        data.put("symbol", symbol);
        HttpUtils.postRequest(BaseUrl.QUERY_DEAL_LIST, getView(), generateRequestHeader(), data,
                new BaseCallback<ResponseBean<List<DealBean>>>(BaseUrl.QUERY_DEAL_LIST, getView(),
                        false) {
                    @Override
                    public void onSuccess(Response<ResponseBean<List<DealBean>>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryDealList(symbol, response.body().data);
                        }
                    }
                });
    }
}

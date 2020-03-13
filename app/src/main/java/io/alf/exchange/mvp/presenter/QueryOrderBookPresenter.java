package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.view.QueryOrderBookView;
import io.cex.mqtt.bean.OrderBookBean;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class QueryOrderBookPresenter extends BasePresenter<QueryOrderBookView> {

    public void queryOrderBook(String symbol, String depthStep) {
        Map<String, Object> data = new HashMap<>();
        data.put("symbol", symbol);
        data.put("depthStep", depthStep);
        HttpUtils.postRequest(BaseUrl.QUERY_ORDER_BOOK, getView(), generateRequestHeader(), data,
                new BaseCallback<ResponseBean<OrderBookBean>>(BaseUrl.QUERY_ORDER_BOOK, getView(),
                        false) {
                    @Override
                    public void onSuccess(Response<ResponseBean<OrderBookBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryOrderBook(response.body().data);
                        }
                    }
                });
    }
}

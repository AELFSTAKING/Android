package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.OrderDetailBean;
import io.alf.exchange.mvp.view.QueryOrderDetailView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryOrderDetailPresenter extends BasePresenter<QueryOrderDetailView> {

    public void queryOrderDetail(String orderNo, String address) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", orderNo);
        data.put("address", address);
        HttpUtils.postRequest(BaseUrl.QUERY_ORDER_DETAIL, getView(), generateRequestHeader(),
                generateRequestBody(data),
                new BaseCallback<ResponseBean<OrderDetailBean>>(BaseUrl.QUERY_ORDER_DETAIL,
                        getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<OrderDetailBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryOrderDetail(response.body().data);
                        }
                    }
                });
    }
}

package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.OrderHistoryBean;
import io.alf.exchange.mvp.view.DelegateView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class DelegatePresenter extends BasePresenter<DelegateView> {

    public void queryCurrentOrderList(String address, int pageSize) {
        queryCurrentOrderList(address, pageSize, false);
    }

    public void queryCurrentOrderList(String address, int pageSize, boolean showLoading) {
        Map<String, Object> data = new HashMap<>();
        data.put("address", address);
        data.put("pageIndex", 1);
        data.put("pageSize", pageSize);
        HttpUtils.postRequest(BaseUrl.QUERY_CURRENT_ORDER_LIST, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<OrderHistoryBean>>(
                        BaseUrl.QUERY_CURRENT_ORDER_LIST, getView(), showLoading) {
                    @Override
                    public void onSuccess(
                            Response<ResponseBean<OrderHistoryBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onQueryCurrentOrderList(response.body().isSuccess(),
                                    response.body().data);
                        }
                    }
                });
    }

    public void queryHistoryOrderList(String address, int pageSize) {
        queryHistoryOrderList(address, "", "", pageSize, false);
    }

    public void queryHistoryOrderList(String address, int pageSize, boolean showLoading) {
        queryHistoryOrderList(address, "", "", pageSize, showLoading);
    }

    public void queryHistoryOrderList(String address, String queryBeginDate, String queryEndDate,
            int pageSize, boolean showLoading) {
        Map<String, Object> data = new HashMap<>();
        data.put("address", address);
        data.put("pageIndex", 1);
        data.put("pageSize", pageSize);
        HttpUtils.postRequest(BaseUrl.QUERY_HISTORY_ORDER_LIST, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<OrderHistoryBean>>(
                        BaseUrl.QUERY_HISTORY_ORDER_LIST, getView(), showLoading) {
                    @Override
                    public void onSuccess(
                            Response<ResponseBean<OrderHistoryBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onQueryHistoryOrderList(response.body().isSuccess(),
                                    response.body().data);
                        }
                    }
                });
    }
}

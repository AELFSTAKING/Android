package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.BindAddressListBean;
import io.alf.exchange.mvp.view.QueryBindAddressView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryBindAddressPresenter extends BasePresenter<QueryBindAddressView> {

    public void queryBindAddress(
            int action,
            String platformAddress,
            String bindChain,
            String bindCurrency) {
        Map<String, Object> data = new HashMap<>();
        data.put("platformAddress", platformAddress);
        data.put("bindChain", bindChain);
        data.put("bindCurrency", bindCurrency);
        HttpUtils.postRequest(BaseUrl.QUERY_BIND_ADDRESS, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<BindAddressListBean>>(
                        BaseUrl.QUERY_BIND_ADDRESS) {
                    @Override
                    public void onSuccess(Response<ResponseBean<BindAddressListBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryBindAddress(action, response.body().data);
                        }
                    }
                });
    }
}

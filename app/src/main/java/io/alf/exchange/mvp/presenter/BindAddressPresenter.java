package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.BindAddressListBean;
import io.alf.exchange.mvp.view.BindAddressView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class BindAddressPresenter extends BasePresenter<BindAddressView> {

    public void bindAddress(
            String platformAddress,
            String bindName,
            String bindChain,
            String bindCurrency,
            String bindAddress) {
        Map<String, Object> data = new HashMap<>();
        data.put("platformAddress", platformAddress);
        data.put("bindName", bindName);
        data.put("bindChain", bindChain);
        data.put("bindCurrency", bindCurrency);
        data.put("bindAddress", bindAddress);
        HttpUtils.postRequest(BaseUrl.BIND_ADDRESS, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<BindAddressListBean>>(
                        BaseUrl.BIND_ADDRESS) {
                    @Override
                    public void onSuccess(Response<ResponseBean<BindAddressListBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onBindAddress(response.body().isSuccess());
                        }
                    }
                });
    }
}

package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.BindAddressListBean;
import io.alf.exchange.mvp.view.DeleteBindAddressView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class DeleteBindAddressPresenter extends BasePresenter<DeleteBindAddressView> {

    public void deleteBindAddress(
            String platformAddress,
            String unbindChain,
            String unbindCurrency,
            String unbindAddress) {
        Map<String, Object> data = new HashMap<>();
        data.put("platformAddress", platformAddress);
        data.put("unbindChain", unbindChain);
        data.put("unbindCurrency", unbindCurrency);
        data.put("unbindAddress", unbindAddress);
        HttpUtils.postRequest(BaseUrl.DELETE_BIND_ADDRESS, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<BindAddressListBean>>(
                        BaseUrl.DELETE_BIND_ADDRESS) {
                    @Override
                    public void onSuccess(Response<ResponseBean<BindAddressListBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onDeleteBindAddress(response.body().isSuccess(), response.body().data);
                        }
                    }
                });
    }
}

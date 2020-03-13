package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;

import io.alf.exchange.mvp.bean.DepositAddressBean;
import io.alf.exchange.mvp.view.QueryDepositAddressView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class QueryDepositAddressPresenter extends BasePresenter<QueryDepositAddressView> {

    public void queryDepositAddress(String currency) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("currency", currency);
        HttpUtils.postRequest(BaseUrl.QUERY_DEPOSIT_ADDRESS, getView(), generateGetRequestHeader(),
                generateRequestBody(data),
                new BaseCallback<ResponseBean<DepositAddressBean>>(BaseUrl.QUERY_DEPOSIT_ADDRESS) {
                    @Override
                    public void onSuccess(Response<ResponseBean<DepositAddressBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryDepositAddress(currency, response.body().data);
                        }
                    }
                });
    }
}

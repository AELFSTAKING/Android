package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.CurrencyInfoBean;
import io.alf.exchange.mvp.view.QueryCurrencyInfoView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryCurrencyInfoPresenter extends BasePresenter<QueryCurrencyInfoView> {
    public void queryCurrencyInfo(String currencyCode) {
        Map<String, Object> data = new HashMap<>();
        data.put("currencyCode", currencyCode);
        HttpUtils.postRequest(BaseUrl.QUERY_CURRENCY_INFO, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<CurrencyInfoBean>>(
                        BaseUrl.QUERY_CURRENCY_INFO, getView()) {
                    @Override
                    public void onSuccess(
                            Response<ResponseBean<CurrencyInfoBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onQueryCurrencyInfo(response.body().isSuccess(),
                                    response.body().data);
                        }
                    }
                });
    }
}

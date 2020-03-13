package io.alf.exchange.mvp.presenter;

import android.text.TextUtils;

import com.lzy.okgo.model.Response;

import java.util.HashMap;

import io.alf.exchange.mvp.view.QueryPriceView;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class QueryPricePresenter extends BasePresenter<QueryPriceView> {

    public void queryUsdtPrice(String currency) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("currency", currency);
        HttpUtils.postRequest(BaseUrl.QUERY_PRICE, getView(), data,
                new BaseCallback<ResponseBean<String>>(BaseUrl.QUERY_PRICE) {
                    @Override
                    public void onSuccess(Response<ResponseBean<String>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()
                                && response.body().data != null) {
                            String price = response.body().data;
                            if (!TextUtils.isEmpty(price)) {
                                CexDataPersistenceUtils.putUsdtPrice("eth", currency, price);
                                getView().onQueryUsdtPrice(currency, price);
                            }
                        }
                    }
                });
    }
}

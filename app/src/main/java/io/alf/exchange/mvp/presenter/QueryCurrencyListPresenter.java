package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.mvp.view.QueryCurrencyListView;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class QueryCurrencyListPresenter extends BasePresenter<QueryCurrencyListView> {

    public void queryCurrencyList() {
        Map<String, Object> data = new HashMap<>();
        data.put("data", "{}");
        HttpUtils.postRequest(BaseUrl.QUERY_CURRENCY_LIST, getView(), generateRequestHeader(), data,
                new BaseCallback<ResponseBean<CurrencyBean>>(BaseUrl.QUERY_CURRENCY_LIST,
                        getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<CurrencyBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryCurrencyList(response.body().data);
                            CexDataPersistenceUtils.putSupportCurrencies(response.body().data.list);
                        }
                    }
                });
    }
}

package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.TxHistoryBean;
import io.alf.exchange.mvp.view.QueryTxHistoryView;
import io.alf.exchange.util.StringUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryTxHistoryPresenter extends BasePresenter<QueryTxHistoryView> {

    public void queryTxHistory(String address, String currency, int pageSize) {
        queryTxHistory(address, currency, pageSize, true);
    }

    public void queryTxHistory(String address, String currency, int pageSize, boolean showLoading) {
        Map<String, Object> data = new HashMap<>();
        data.put("address", address);
        data.put("currency", StringUtils.toLowerCase(currency));
        data.put("pageIndex", 1);
        data.put("pageSize", pageSize);
        HttpUtils.postRequest(BaseUrl.QUERY_TX_HISTORY, getView(), generateGetRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<TxHistoryBean>>(
                        BaseUrl.QUERY_TX_HISTORY, getView(), showLoading) {
                    @Override
                    public void onSuccess(Response<ResponseBean<TxHistoryBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryTxHistory(response.body().data);
                        }
                    }
                });
    }
}

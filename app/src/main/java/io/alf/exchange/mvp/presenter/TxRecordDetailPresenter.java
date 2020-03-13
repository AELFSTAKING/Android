package io.alf.exchange.mvp.presenter;

import com.google.gson.Gson;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.util.HashMap;

import io.alf.exchange.mvp.bean.TxInfoBean;
import io.alf.exchange.mvp.view.TxRecordDetailView;
import io.alf.exchange.util.StringUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class TxRecordDetailPresenter extends BasePresenter<TxRecordDetailView> {

    public void queryTxInfo(
            String chain,
            String currency,
            String hash) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", StringUtils.toLowerCase(chain));

        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", StringUtils.toLowerCase(chain));
        params.put("currency", currency);

        HashMap<String, Object> data = new HashMap<>();
        data.put("hash", hash);

        params.put("data", new Gson().toJson(data));

        HttpUtils.postRequest(BaseUrl.QUERY_TX_INFO, getView(), headers, params,
                new BaseCallback<ResponseBean<TxInfoBean>>(
                        BaseUrl.QUERY_TX_INFO, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<TxInfoBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onQueryTxInfo(response.body().isSuccess(),
                                    response.body().data);
                        }
                    }
                });
    }
}

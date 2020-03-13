package io.alf.exchange.mvp.presenter;

import android.text.TextUtils;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

import io.alf.exchange.mvp.bean.BalanceBean;
import io.alf.exchange.mvp.view.QueryBalanceView;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;
import io.tick.base.util.BigDecimalUtil;


public class QueryBalancePresenter extends BasePresenter<QueryBalanceView> {

    public void queryBalance(String chain, String currency, String address) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", "eth");
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", "eth");
        params.put("currency", currency);
        HashMap<String, Object> data = new HashMap<>();
        data.put("address", address);
        params.put("data", new JSONObject(data));
        HttpUtils.postRequest(BaseUrl.QUERY_BALANCE, getView(), headers, params,
                new BaseCallback<ResponseBean<BalanceBean>>(BaseUrl.QUERY_BALANCE) {
                    @Override
                    public void onSuccess(Response<ResponseBean<BalanceBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()
                                && response.body().data != null) {
                            BalanceBean data = response.body().data;
                            if (!TextUtils.isEmpty(data.amount)) {
                                data.amount = BigDecimalUtil.formatByDecimalValue(data.amount);
                                getView().onQueryBalance(chain, currency, data.amount);
                                CexDataPersistenceUtils.putBalance(chain, currency, data.amount);
                            }
                        }
                    }
                });
    }
}

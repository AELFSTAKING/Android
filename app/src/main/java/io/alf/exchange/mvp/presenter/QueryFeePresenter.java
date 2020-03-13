package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

import io.alf.exchange.mvp.bean.FeeBean;
import io.alf.exchange.mvp.view.QueryFeeView;
import io.alf.exchange.util.StringUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryFeePresenter extends BasePresenter<QueryFeeView> {

    public void queryFee(String chain, String currency, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", StringUtils.toLowerCase(chain));

        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", StringUtils.toLowerCase(chain));
        params.put("currency", StringUtils.toLowerCase(currency));

        HashMap<String, Object> data = new HashMap<>();
        //lock,approve,transfer
        data.put("type", type);

        params.put("data", new JSONObject(data));
        HttpUtils.postRequest(BaseUrl.QUERY_TRANSFER_FEE, getView(), headers, params,
                new BaseCallback<ResponseBean<FeeBean>>(
                        BaseUrl.QUERY_TRANSFER_FEE) {
                    @Override
                    public void onSuccess(Response<ResponseBean<FeeBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryFee(response.body().data);
                        }
                    }
                });
    }

    public void queryTransferFee(String chain, String currency) {
        queryFee(chain, currency, "transfer");
    }

    public void queryWithdrawFee(String chain, String currency) {
        queryFee(chain, currency, "approve");
    }
}

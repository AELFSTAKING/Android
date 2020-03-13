package io.alf.exchange.mvp.presenter;

import com.google.gson.Gson;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.util.HashMap;

import io.alf.exchange.mvp.bean.CreateTxBean;
import io.alf.exchange.mvp.bean.SendTxBean;
import io.alf.exchange.mvp.view.TransferView;
import io.alf.exchange.util.StringUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class TransferPresenter extends BasePresenter<TransferView> {

    public void createTransferTx(
            String chain,
            String currency,
            String amount,
            String receiver,
            String sender,
            String gasPrice,
            String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", StringUtils.toLowerCase(chain));

        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", StringUtils.toLowerCase(chain));
        params.put("currency", currency);

        HashMap<String, Object> data = new HashMap<>();
        data.put("amount", amount);
        data.put("receiver", receiver);
        data.put("sender", sender);
        data.put("gasPrice", gasPrice);

        params.put("data", new Gson().toJson(data));

        HttpUtils.postRequest(BaseUrl.CREATE_TRANSFER_TX, getView(), headers, params,
                new BaseCallback<ResponseBean<CreateTxBean>>(
                        BaseUrl.CREATE_TRANSFER_TX, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<CreateTxBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onCreateTransferTx(response.body().isSuccess(),
                                    response.body().data, password);
                        }
                    }
                });
    }

    public void sendTransferTx(
            String chain,
            String currency,
            String signedRawTransaction) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", StringUtils.toLowerCase(chain));

        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", StringUtils.toLowerCase(chain));
        params.put("currency", currency);

        HashMap<String, Object> data = new HashMap<>();
        data.put("signedRawTransaction", signedRawTransaction);

        params.put("data", new Gson().toJson(data));

        HttpUtils.postRequest(BaseUrl.SEND_TRANSFER_TX, getView(), headers, params,
                new BaseCallback<ResponseBean<SendTxBean>>(
                        BaseUrl.SEND_TRANSFER_TX, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<SendTxBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onSendTransferTx(response.body().isSuccess(),
                                    response.body().data);
                        }
                    }
                });
    }
}

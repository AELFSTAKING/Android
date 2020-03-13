package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.CancelOrderBean;
import io.alf.exchange.mvp.bean.SendTxBean;
import io.alf.exchange.mvp.view.CancelOrderView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class CancelOrderPresenter extends BasePresenter<CancelOrderView> {

    public void cancelUserOrder(String orderNo, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", orderNo);
        HttpUtils.postRequest(BaseUrl.CANCEL_USER_ORDER, getView(), generateRequestHeader(),
                generateRequestBody(new HashMap<>(data)),
                new BaseCallback<ResponseBean<CancelOrderBean>>(BaseUrl.CANCEL_USER_ORDER,
                        getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<CancelOrderBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onCancelUserOrder(response.body().isSuccess(), response.body().data,
                                    password);
                        }
                    }
                });
    }

    public void sendCancelOrderTx(String orderId, String signedRawTransaction) {
        HttpHeaders headers = generateRequestHeader();
        headers.put("chain", "eth");
        headers.put("currency", "eth");
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("signedRawTransaction", signedRawTransaction);
        HttpUtils.postRequest(BaseUrl.SEND_CANCEL_ORDER_TX, getView(), headers, data,
                new BaseCallback<ResponseBean<SendTxBean>>(BaseUrl.SEND_CANCEL_ORDER_TX,
                        getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<SendTxBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onSendCancelOrderTx(orderId, response.body().data);
                        }
                    }
                });
    }

    public void cancelCallback(String orderNo, String txHash) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", orderNo);
        data.put("txHash", txHash);
        HttpUtils.postRequest(BaseUrl.CANCEL_CALLBACK, getView(), generateRequestHeader(), data,
                new BaseCallback<ResponseBean<String>>(BaseUrl.CANCEL_CALLBACK, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<String>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onCancelCallback(orderNo, response.body().isSuccess());
                        }
                    }
                });
    }
}

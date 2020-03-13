package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.CreateOrderBean;
import io.alf.exchange.mvp.bean.SendTxBean;
import io.alf.exchange.mvp.view.CreateOrderView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class CreateOrderPresenter extends BasePresenter<CreateOrderView> {

    public void createUserOrder(String password, HashMap<String, String> data) {
        HttpUtils.postRequest(BaseUrl.CREATE_ORDER, getView(), generateRequestHeader(),
                generateRequestBody(new HashMap<>(data)),
                new BaseCallback<ResponseBean<CreateOrderBean>>(BaseUrl.CREATE_ORDER, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<CreateOrderBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onCreateUserOrder(password, response.body().data);
                        }
                    }
                });
    }

    public void sendOrderTx(String orderId, String signedRawTransaction) {
        HttpHeaders headers = generateRequestHeader();
        headers.put("chain", "eth");
        headers.put("currency", "eth");
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("signedRawTransaction", signedRawTransaction);
        HttpUtils.postRequest(BaseUrl.SEND_ORDER_TX, getView(), headers, data,
                new BaseCallback<ResponseBean<SendTxBean>>(BaseUrl.CREATE_ORDER,
                        getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<SendTxBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onSendOrderTx(orderId, response.body().data);
                        }
                    }
                });
    }

    public void payCallback(String orderNo, String txHash) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", orderNo);
        data.put("txHash", txHash);
        HttpUtils.postRequest(BaseUrl.PAY_CALLBACK, getView(), generateRequestHeader(), data,
                new BaseCallback<ResponseBean<String>>(BaseUrl.PAY_CALLBACK, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<String>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onPayCallback(orderNo, response.body().isSuccess());
                        }
                    }
                });
    }
}

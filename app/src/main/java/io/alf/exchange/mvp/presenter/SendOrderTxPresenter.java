package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.SendTxBean;
import io.alf.exchange.mvp.view.SendOrderTxView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class SendOrderTxPresenter extends BasePresenter<SendOrderTxView> {

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
}

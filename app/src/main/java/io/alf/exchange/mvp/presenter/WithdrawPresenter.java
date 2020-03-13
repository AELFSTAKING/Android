package io.alf.exchange.mvp.presenter;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.Constant;
import io.alf.exchange.mvp.bean.WithdrawOrderBean;
import io.alf.exchange.mvp.presenter.eth.KofoUrl;
import io.alf.exchange.mvp.presenter.eth.tx.EthCreateTx;
import io.alf.exchange.mvp.presenter.eth.tx.EthSendTx;
import io.alf.exchange.mvp.view.WithdrawView;
import io.alf.exchange.util.StringUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.callbck.JsonCallback;
import io.tick.base.net.response.ResponseBean;

public class WithdrawPresenter extends BasePresenter<WithdrawView> {

    public void createWithdrawOrder(
            String chain,
            String currency,
            String amount,
            String fromAddress,
            String toAddress,
            String takerSignature,
            String gasPrice,
            String password) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("kofoId", Constant.KOFO_ID);
        data.put("chain", chain);
        data.put("currency", currency);
        data.put("amount", amount);
        data.put("fromAddress", fromAddress);
        data.put("toAddress", toAddress);
        data.put("takerSignature", takerSignature);
        HttpUtils.postRequest(BaseUrl.CREATE_WITHDRAW_ORDER, getView(), generateRequestHeader(),
                generateRequestBody(data),
                new BaseCallback<ResponseBean<WithdrawOrderBean>>(
                        BaseUrl.CREATE_WITHDRAW_ORDER, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<WithdrawOrderBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onCreateWithdrawOrder(response.body().isSuccess(),
                                    response.body().data, fromAddress, gasPrice, password);
                        }
                    }
                });
    }

    /**
     * 创建授权交易
     *
     * @param settlementId 订单Id
     * @param chain        创建授权交易的链
     * @param currency     币种
     * @param amount       金额
     * @param sender       交易的发送者
     */
    public void createApproveTx(
            String settlementId,
            String chain,
            String currency,
            String amount,
            String sender,
            String gasPrice,
            String withdrawNo,
            String password) {
        // 构建http header
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", StringUtils.toLowerCase(chain));
        // 构建http body
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", StringUtils.toLowerCase(chain));
        params.put("currency", StringUtils.toLowerCase(currency));
        HashMap<String, Object> data = new HashMap<>();
        data.put("amount", amount);
        data.put("sender", sender);
        data.put("settlementId", settlementId);
        data.put("gasPrice", gasPrice);
        params.put("data", new Gson().toJson(data));
        // 发起http请求
        HttpUtils.postRequest(KofoUrl.CREATE_APPROVE_TX, getView(), headers, params,
                new JsonCallback<ResponseBean<EthCreateTx>>(KofoUrl.CREATE_APPROVE_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthCreateTx>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onCreateApproveTx(
                                    response.body().isSuccess(),
                                    response.body().data,
                                    settlementId,
                                    chain,
                                    currency,
                                    withdrawNo,
                                    password);
                        }
                    }
                });
    }

    /**
     * 发送授权交易
     *
     * @param settlementId         订单Id
     * @param chain                权交易的链
     * @param currency             币种
     * @param signedRawTransaction 签名后交易数据
     */
    public void sendApproveTx(
            String settlementId,
            String chain,
            String currency,
            String signedRawTransaction,
            String withdrawNo) {
        // 构建http header
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", StringUtils.toLowerCase(chain));
        // 构建http body
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", StringUtils.toLowerCase(chain));
        params.put("currency", StringUtils.toLowerCase(currency));
        HashMap<String, Object> data = new HashMap<>();
        data.put("signedRawTransaction", signedRawTransaction);
        data.put("settlementId", settlementId);
        params.put("data", new Gson().toJson(data));
        // 发起http请求
        HttpUtils.postRequest(KofoUrl.SEND_APPROVE_TX, getView(), headers, params,
                new JsonCallback<ResponseBean<EthSendTx>>(KofoUrl.SEND_APPROVE_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthSendTx>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onSendApproveTx(response.body().isSuccess(),
                                    settlementId, response.body().data, withdrawNo);
                        }
                    }
                });
    }


    public void withdrawCallback(String withdrawNo, String txHash) {
        Map<String, Object> data = new HashMap<>();
        data.put("withdrawNo", withdrawNo);
        data.put("withdrawTxId", txHash);
        HttpUtils.postRequest(BaseUrl.WITHDRAW_CALLBACK, getView(), generateRequestHeader(), data,
                new BaseCallback<ResponseBean<String>>(BaseUrl.WITHDRAW_CALLBACK, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<String>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            //getView().onPayCallback(orderNo, response.body().isSuccess());
                        }
                    }
                });
    }

    public void takerSubmitApproveCallback(
            String settlementId,
            String takerChain,
            String takerApproveTransactionId,
            String signature) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("settlementId", settlementId);
        params.put("takerApproveTransactionId", takerApproveTransactionId);
        params.put("signature", signature);
        String url = replaceChainInUrl(KofoUrl.CB_TAKER_SUBMIT_APPROVE,
                takerChain);

        HttpUtils.postRequest(url, getView(), params,
                new BaseCallback<ResponseBean<Object>>(url, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<Object>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            //getView().onTakerSubmitApproveCallback(settlementId);
                        }
                    }
                });
    }

    private static String replaceChainInUrl(String url, String chain) {
        if (!TextUtils.isEmpty(url) && url.contains("{chain}")) {
            return url.replace("{chain}", StringUtils.toLowerCase(chain));
        }
        return url;
    }
}

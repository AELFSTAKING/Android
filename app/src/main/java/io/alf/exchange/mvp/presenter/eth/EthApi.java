package io.alf.exchange.mvp.presenter.eth;

import com.google.gson.Gson;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.util.HashMap;

import io.alf.exchange.mvp.presenter.eth.tx.EthCreateLockTx;
import io.alf.exchange.mvp.presenter.eth.tx.EthCreateTx;
import io.alf.exchange.mvp.presenter.eth.tx.EthSendTx;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.callbck.JsonCallback;
import io.tick.base.net.response.ResponseBean;


public class EthApi {

    private static final String TAG = EthApi.class.getSimpleName();

    /**
     * 创建锁定交易
     *
     * @param settlementId 订单Id
     * @param chain        创建锁定交易的链
     * @param currency     锁定币种
     * @param amount       锁定金额
     * @param hValue       用于创建锁定交易的H值
     * @param lockTime     锁定时间
     * @param sender       交易的发送者
     * @param receiver     交易的接收者
     * @param callback     回调
     */
    public static void createLockTx(
            String settlementId,
            String chain,
            String currency,
            String amount,
            String hValue,
            long lockTime,
            String sender,
            String receiver,
            IThruster.Callback<EthCreateLockTx> callback) {
        // 构建http header
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", chain);
        // 构建http body
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", chain);
        params.put("currency", currency);
        HashMap<String, Object> data = new HashMap<>();
        data.put("amount", amount);
        data.put("settlementId", settlementId);
        data.put("hValue", hValue);
        data.put("lockTime", lockTime);
        data.put("receiver", receiver);
        data.put("sender", sender);
        params.put("data", new Gson().toJson(data));
        // 发起http请求
        HttpUtils.postRequest(KofoUrl.CREATE_LOCK_TX, TAG, headers, params,
                new JsonCallback<ResponseBean<EthCreateLockTx>>(KofoUrl.CREATE_LOCK_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthCreateLockTx>> response) {
                        if ("000000".equals(response.body().code) && callback != null) {
                            callback.onSuccess(response.body().data);
                        }
                    }
                });
    }


    /**
     * 发送锁定交易
     *
     * @param settlementId         订单Id
     * @param chain                锁定交易的链
     * @param currency             锁定币种
     * @param lockId               锁Id
     * @param signedRawTransaction 签名后的交易
     * @param callback             回调
     */
    public static void sendLockTx(String settlementId, String chain, String currency, String lockId,
            String signedRawTransaction, IThruster.Callback<EthSendTx> callback) {
        // 构建http header
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", chain);
        // 构建http body
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", chain);
        params.put("currency", currency);
        HashMap<String, Object> data = new HashMap<>();
        data.put("lockId", lockId);
        data.put("settlementId", settlementId);
        data.put("signedRawTransaction", signedRawTransaction);
        params.put("data", new Gson().toJson(data));
        // 发起http请求
        HttpUtils.postRequest(KofoUrl.SEND_LOCK_TX, TAG, headers, params,
                new JsonCallback<ResponseBean<EthSendTx>>(KofoUrl.SEND_LOCK_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthSendTx>> response) {
                        if ("000000".equals(response.body().code) && callback != null) {
                            callback.onSuccess(response.body().data);
                        }
                    }
                });
    }

    /**
     * 创建提现交易
     *
     * @param settlementId 订单Id
     * @param chain        交易链
     * @param currency     交易币种
     * @param lockId       锁定Id
     * @param preimage     密钥
     * @param sender       提现发起人地址
     * @param callback     回调
     */
    public static void createWithdrawTx(String settlementId, String chain, String currency,
            String lockId, String preimage, String sender,
            IThruster.Callback<EthCreateTx> callback) {
        // 构建http header
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", chain);
        // 构建http body
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", chain);
        params.put("currency", currency);
        HashMap<String, Object> data = new HashMap<>();
        data.put("lockId", lockId);
        data.put("settlementId", settlementId);
        data.put("preimage", preimage);
        data.put("sender", sender);
        params.put("data", new Gson().toJson(data));
        // 发起http请求
        HttpUtils.postRequest(KofoUrl.CREATE_WITHDRAW_TX, TAG, headers, params,
                new JsonCallback<ResponseBean<EthCreateTx>>(KofoUrl.CREATE_WITHDRAW_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthCreateTx>> response) {
                        if ("000000".equals(response.body().code) && callback != null) {
                            callback.onSuccess(response.body().data);
                        }
                    }
                });
    }

    /**
     * 发送提现交易
     *
     * @param settlementId         订单Id
     * @param chain                交易链
     * @param currency             交易币种
     * @param signedRawTransaction 签名后的交易
     * @param callback             回调
     */
    public static void sendWithdrawTx(String settlementId, String chain, String currency,
            String signedRawTransaction, IThruster.Callback<EthSendTx> callback) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", chain);
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", chain);
        params.put("currency", currency);
        HashMap<String, Object> data = new HashMap<>();
        data.put("settlementId", settlementId);
        data.put("signedRawTransaction", signedRawTransaction);
        params.put("data", new Gson().toJson(data));
        // 回调状态服务器
        HttpUtils.postRequest(KofoUrl.SEND_WITHDRAW_TX, TAG, headers, params,
                new JsonCallback<ResponseBean<EthSendTx>>(KofoUrl.SEND_WITHDRAW_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthSendTx>> response) {
                        if ("000000".equals(response.body().code) && callback != null) {
                            callback.onSuccess(response.body().data);
                        }
                    }
                });
    }

    /**
     * 创建赎回交易
     *
     * @param settlementId 订单Id
     * @param chain        交易链
     * @param currency     交易币种
     * @param lockId       锁定Id
     * @param sender       赎回发起人地址
     * @param callback     回调
     */
    public static void createRefundTx(String settlementId, String chain, String currency,
            String lockId, String sender, IThruster.Callback<EthCreateTx> callback) {
        // 构建http header
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", chain);
        // 构建http body
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", chain);
        params.put("currency", currency);
        HashMap<String, Object> data = new HashMap<>();
        data.put("lockId", lockId);
        data.put("settlementId", settlementId);
        data.put("sender", sender);
        params.put("data", new Gson().toJson(data));
        // 发起http请求
        HttpUtils.postRequest(KofoUrl.CREATE_REFUND_TX, TAG, headers, params,
                new JsonCallback<ResponseBean<EthCreateTx>>(KofoUrl.CREATE_REFUND_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthCreateTx>> response) {
                        if ("000000".equals(response.body().code) && callback != null) {
                            callback.onSuccess(response.body().data);
                        }
                    }
                });
    }

    /**
     * 发送提现交易
     *
     * @param settlementId         订单Id
     * @param chain                交易链
     * @param currency             交易币种
     * @param signedRawTransaction 签名后的交易
     * @param callback             回调
     */
    public static void sendRefundTx(String settlementId, String chain, String currency,
            String signedRawTransaction, IThruster.Callback<EthSendTx> callback) {
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", chain);
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", chain);
        params.put("currency", currency);
        HashMap<String, Object> data = new HashMap<>();
        data.put("settlementId", settlementId);
        data.put("signedRawTransaction", signedRawTransaction);
        params.put("data", new Gson().toJson(data));
        // 回调状态服务器
        HttpUtils.postRequest(KofoUrl.SEND_REFUND_TX, TAG, headers, params,
                new JsonCallback<ResponseBean<EthSendTx>>(KofoUrl.SEND_REFUND_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthSendTx>> response) {
                        if ("000000".equals(response.body().code) && callback != null) {
                            callback.onSuccess(response.body().data);
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
     * @param callback     回调
     */
    public static void createApproveTx(String settlementId, String chain, String currency,
            String amount, String sender, IThruster.Callback<EthCreateTx> callback) {
        // 构建http header
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", chain);
        // 构建http body
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", chain);
        params.put("currency", currency);
        HashMap<String, Object> data = new HashMap<>();
        data.put("amount", amount);
        data.put("sender", sender);
        data.put("settlementId", settlementId);
        params.put("data", new Gson().toJson(data));
        // 发起http请求
        HttpUtils.postRequest(KofoUrl.CREATE_APPROVE_TX, TAG, headers, params,
                new JsonCallback<ResponseBean<EthCreateTx>>(KofoUrl.CREATE_APPROVE_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthCreateTx>> response) {
                        if ("000000".equals(response.body().code) && callback != null) {
                            callback.onSuccess(response.body().data);
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
     * @param callback             回调
     */
    public static void sendApproveTx(String settlementId, String chain, String currency,
            String signedRawTransaction, IThruster.Callback<EthSendTx> callback) {
        // 构建http header
        HttpHeaders headers = new HttpHeaders();
        headers.put("chain", chain);
        // 构建http body
        HashMap<String, Object> params = new HashMap<>();
        params.put("chain", chain);
        params.put("currency", currency);
        HashMap<String, Object> data = new HashMap<>();
        data.put("signedRawTransaction", signedRawTransaction);
        data.put("settlementId", settlementId);
        params.put("data", new Gson().toJson(data));
        // 发起http请求
        HttpUtils.postRequest(KofoUrl.SEND_APPROVE_TX, TAG, headers, params,
                new JsonCallback<ResponseBean<EthSendTx>>(KofoUrl.SEND_APPROVE_TX) {
                    @Override
                    public void onSuccess(Response<ResponseBean<EthSendTx>> response) {
                        if ("000000".equals(response.body().code) && callback != null) {
                            callback.onSuccess(response.body().data);
                        }
                    }
                });
    }
}

package io.alf.exchange.mvp.presenter.eth;

import io.tick.base.BuildConfig;

public class KofoUrl {

    public final static String KOFO_URL = BuildConfig.kofoUrl;
    // Maker生成h值与赎回交易回调
    public final static String CB_MAKER_CREATE_REFUND_TX_AND_H =
            KOFO_URL + "settlement-server/status/{chain}/maker/createRefundTxAndHCallback";
    // Taker获取h值及生成赎回交易回调
    public final static String CB_TAKER_RECEIVE_H_AND_CREATE_REFUND =
            KOFO_URL + "/settlement-server/status/{chain}/taker/receiveHAndCreateRefundCallback";
    // Maker提交锁定交易回调
    public final static String CB_MAKER_SUBMIT_HASH_LOCK =
            KOFO_URL + "/settlement-server/status/{chain}/maker/submitHashLockCallback";
    // Taker提交锁定交易回调
    public final static String CB_TAKER_SUBMIT_HASH_LOCK =
            KOFO_URL + "/settlement-server/status/{chain}/taker/submitHashLockCallback";
    // Maker提交授权交易回调
    public final static String CB_MAKER_SUBMIT_APPROVE =
            KOFO_URL + "/settlement-server/status/{chain}/maker/submitApproveCallback";
    // Taker提交授权交易回调
    public final static String CB_TAKER_SUBMIT_APPROVE =
            KOFO_URL + "/settlement-server/status/{chain}/taker/submitApproveCallback";
    // Maker提交提现交易回调
    public final static String CB_MAKER_SUBMIT_WITHDRAW =
            KOFO_URL + "/settlement-server/status/{chain}/maker/submitWithdrawCallback";
    // Taker提交提现交易回调
    public final static String CB_TAKER_SUBMIT_WITHDRAW =
            KOFO_URL + "/settlement-server/status/{chain}/taker/submitWithdrawCallback";
    // Maker提交赎回交易回调
    public final static String CB_MAKER_SUBMIT_REFUND =
            KOFO_URL + "/settlement-server/status/{chain}/maker/submitRefundCallback";
    // Taker提交赎回交易回调
    public final static String CB_TAKER_SUBMIT_REFUND =
            KOFO_URL + "/settlement-server/status/{chain}/taker/submitRefundCallback";

    // Maker终态callback
    public final static String CB_MAKER_FINAL_STATUS =
            KOFO_URL + "/settlement-server/status/{chain}/maker/finalStatusCallback";
    // Taker终态callback
    public final static String CB_TAKER_FINAL_STATUS =
            KOFO_URL + "/settlement-server/status/{chain}/taker/finalStatusCallback";


    //public final static String CB_TAKER_FINAL_STATUS = BASE_URL +
    // "/settlement-server/status/{chain}/taker/finalStatusCallback";

    // 创建锁定交易
    public final static String CREATE_LOCK_TX = KOFO_URL + "gateway/create_lock_tx";
    // 发送锁定交易
    public final static String SEND_LOCK_TX = KOFO_URL + "gateway/send_lock_tx";
    // 创建授权交易
    public final static String CREATE_APPROVE_TX = KOFO_URL + "gateway/create_approve_tx";
    // 发送授权交易
    public final static String SEND_APPROVE_TX = KOFO_URL + "gateway/send_approve_tx";
    // 创建提现交易
    public final static String CREATE_WITHDRAW_TX = KOFO_URL + "gateway/create_withdraw_tx";
    // 发送提现交易
    public final static String SEND_WITHDRAW_TX = KOFO_URL + "gateway/send_withdraw_tx";
    // 创建赎回交易
    public final static String CREATE_REFUND_TX = KOFO_URL + "gateway/create_refund_tx";
    // 发送赎回交易
    public final static String SEND_REFUND_TX = KOFO_URL + "gateway/send_refund_tx";

}

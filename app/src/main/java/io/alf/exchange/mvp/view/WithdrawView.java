package io.alf.exchange.mvp.view;

import io.alf.exchange.mvp.bean.WithdrawOrderBean;
import io.alf.exchange.mvp.presenter.eth.tx.EthCreateTx;
import io.alf.exchange.mvp.presenter.eth.tx.EthSendTx;
import io.tick.base.mvp.IView;

public interface WithdrawView extends IView {
    void onCreateWithdrawOrder(boolean success, WithdrawOrderBean data, String sender,
            String gasPrice, String password);

    void onCreateApproveTx(boolean success, EthCreateTx data, String settlementId, String chain,
            String currency, String withdrawNo, String password);

    void onSendApproveTx(boolean success, String settlementId, EthSendTx data, String withdrawNo);
}

package io.alf.exchange.mvp.view;

import io.alf.exchange.mvp.bean.CreateTxBean;
import io.alf.exchange.mvp.bean.SendTxBean;
import io.tick.base.mvp.IView;

public interface TransferView extends IView {
    void onCreateTransferTx(boolean success, CreateTxBean data, String password);

    void onSendTransferTx(boolean success, SendTxBean data);
}

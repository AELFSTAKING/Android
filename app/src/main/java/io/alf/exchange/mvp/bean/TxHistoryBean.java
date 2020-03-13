package io.alf.exchange.mvp.bean;

import java.io.Serializable;
import java.util.List;

public class TxHistoryBean implements Serializable {

    public List<TransactionsBean> transactions;

    public static class TransactionsBean implements Serializable {
        public String blockHash;
        public String blockHeight;
        public String currency;
        public String hash;
        public String receiver;
        public String withdrawReceiver;
        public String sendTimestamp;
        public String sender;
        public String status;
        public String tag;
        public String timestamp;
        // DEPOSIT 充值；WITHDRAW 提现；TRANSFER 转账；RECEIPT 收款
        public String txType;
        public String value;
        public String url;
    }
}

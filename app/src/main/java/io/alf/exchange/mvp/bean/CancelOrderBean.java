package io.alf.exchange.mvp.bean;

public class CancelOrderBean {

    public CreateTxBean createStackingTxResp;
    public OrderCancelMessage orderCancelMessage;

    public static class CreateOrderTxRespBean {
        public String blockHeight;
        public String gasLimit;
        public String gasPrice;
        public String nonce;
        public String rawTransaction;
    }

    public static class OrderCancelMessage {
        public String orderNo;
        public String status;
        public String symbol;
        public String txId;
        public long gmtCreate;
        public long gmtModified;
    }
}

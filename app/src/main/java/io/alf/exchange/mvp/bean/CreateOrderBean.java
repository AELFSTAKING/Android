package io.alf.exchange.mvp.bean;

public class CreateOrderBean {

    public CreateTxBean createOrderTxResp;
    public OrderBean order;

    public static class OrderBean {
        public int action;
        public String address;
        public String amount;
        public String amountRemaining;
        public String chain;
        public String counterChain;
        public String counterChainAddress;
        public String counterCurrency;
        public String currency;
        public String fee;
        public String feeCurrency;
        public String fromClientType;
        public String groupCode;
        public String level;
        public String makerFeeRate;
        public String orderNo;
        public int orderType;
        public String priceAverage;
        public String priceLimit;
        public String productCode;
        public String quantity;
        public String quantityRemaining;
        public int state;
        public int status;
        public String symbol;
        public String takerFeeRate;
        public String txId;
        public long utcCreate;
        public long utcUpdate;
    }
}

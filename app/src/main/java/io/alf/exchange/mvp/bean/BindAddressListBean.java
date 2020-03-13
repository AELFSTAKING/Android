package io.alf.exchange.mvp.bean;

import java.util.List;

public class BindAddressListBean {

    public String platformAddress;
    public List<AddressListBean> addressList;

    public static class AddressListBean {
        public String address;
        public String chain;
        public String currency;
        public long gmtCreate;
        public long gmtModified;
        public String platformAddress;
        public String platformChain;
        public String name;
        public String status;
    }
}

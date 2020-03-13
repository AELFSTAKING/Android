package io.alf.exchange.mvp.bean;

import java.io.Serializable;
import java.util.List;

public class RewardRecordBean implements Serializable {

    public String rewardType;
    public String sendTime;
    public List<SendListBean> sendList;

    public static class SendListBean {
        public String rewardAmount;
        public String rewardCurrency;
        public String tokenCurrency;
    }
}

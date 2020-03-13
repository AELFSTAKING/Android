package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.MiningRewardBean;
import io.alf.exchange.util.StringUtils;

public class RewardMiningAdapter extends BaseQuickAdapter<MiningRewardBean, BaseViewHolder> {

    public RewardMiningAdapter() {
        super(R.layout.item_reward_mining);
    }

    @Override
    protected void convert(BaseViewHolder helper, MiningRewardBean item) {
        helper.setText(R.id.tv_currency, StringUtils.toUpperCase(item.currency));
        helper.setText(R.id.tv_balance,
                item.balance + " " + StringUtils.toUpperCase(item.currency));
        helper.setText(R.id.tv_power,
                item.power + " " + "算力");
        helper.setText(R.id.tv_reward,
                item.sendedReward + " " + StringUtils.toUpperCase(item.rewardCurrency));
        helper.setText(R.id.tv_unsend_reward,
                item.unsendReward + " " + StringUtils.toUpperCase(item.rewardCurrency));
    }
}

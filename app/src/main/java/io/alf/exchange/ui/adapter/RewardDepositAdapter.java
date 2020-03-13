package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.DepositRewardBean;
import io.alf.exchange.util.StringUtils;

public class RewardDepositAdapter extends BaseQuickAdapter<DepositRewardBean, BaseViewHolder> {

    public RewardDepositAdapter() {
        super(R.layout.item_reward_deposit);
    }

    @Override
    protected void convert(BaseViewHolder helper, DepositRewardBean item) {
        helper.setText(R.id.tv_currency, StringUtils.toUpperCase(item.currency));
        helper.setText(R.id.tv_total,
                item.depositTotal + " " + StringUtils.toUpperCase(item.currency));
        helper.setText(R.id.tv_balance,
                item.balance + " " + StringUtils.toUpperCase(item.currency));
        helper.setText(R.id.tv_reward,
                item.sendedReward + " " + StringUtils.toUpperCase(item.rewardCurrency));
        helper.setText(R.id.tv_unsend_reward,
                item.unsendReward + " " + StringUtils.toUpperCase(item.rewardCurrency));
    }
}

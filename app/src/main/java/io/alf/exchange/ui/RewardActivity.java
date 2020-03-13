package io.alf.exchange.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.DepositRewardBean;
import io.alf.exchange.mvp.bean.MiningRewardBean;
import io.alf.exchange.mvp.presenter.RewardPresenter;
import io.alf.exchange.mvp.view.RewardView;
import io.alf.exchange.ui.adapter.RewardDepositAdapter;
import io.alf.exchange.ui.adapter.RewardMiningAdapter;
import io.alf.exchange.util.EmptyViewUtil;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.DensityUtil;
import io.tick.base.util.RxBindingUtils;

public class RewardActivity extends MvpActivity implements RewardView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvMenu;
    @BindView(R.id.tv_reward_record)
    TextView tvRewardRecord;
    @BindView(R.id.recycler_view_1)
    RecyclerView recyclerView1;
    @BindView(R.id.recycler_view_2)
    RecyclerView recyclerView2;
    @BindView(R.id.rg_reward)
    RadioGroup rgReward;

    private RewardDepositAdapter mRewardDepositAdapter;
    private RewardMiningAdapter mRewardMiningAdapter;

    private RewardPresenter mRewardPresenter;

    @Override
    protected void initPresenter() {
        mRewardPresenter = registerPresenter(new RewardPresenter(), this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reward;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("奖励");
        tvMenu.setText("规则");
        tvMenu.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_rule, 0, 0, 0);
        tvMenu.setCompoundDrawablePadding(DensityUtil.dp2px(5));
        initAdapter();
    }

    private void initAdapter() {
        mRewardDepositAdapter = new RewardDepositAdapter();
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(mRewardDepositAdapter);

        mRewardMiningAdapter = new RewardMiningAdapter();
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setAdapter(mRewardMiningAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address)) {
            mRewardPresenter.queryDepositReward(address);
            mRewardPresenter.queryMiningReward(address);
        }
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            ActivityStartUtils.jump(this, RewardRecordActivity.class);
        }, tvRewardRecord);
        RxBindingUtils.clicks(v -> {
            ActivityStartUtils.jump(this, RewardRuleActivity.class);
        }, tvMenu);
        rgReward.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_deposit_reward: {
                        recyclerView1.setVisibility(View.VISIBLE);
                        recyclerView2.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.rb_mining_reward: {
                        recyclerView1.setVisibility(View.GONE);
                        recyclerView2.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });
        rgReward.check(R.id.rb_deposit_reward);
    }

    @Override
    public void onQueryDepositReward(boolean success, List<DepositRewardBean> data) {
        if (success) {
            if (data != null && data.size() > 0) {
                mRewardDepositAdapter.setNewData(data);
            } else {
                mRewardDepositAdapter.setNewData(null);
                mRewardDepositAdapter.setEmptyView(
                        EmptyViewUtil.setEmpty(recyclerView1, this, "暂无数据", 100));
            }
        }
    }

    @Override
    public void onQueryMiningReward(boolean success, List<MiningRewardBean> data) {
        if (success) {
            if (data != null && data.size() > 0) {
                mRewardMiningAdapter.setNewData(data);
            } else {
                mRewardMiningAdapter.setNewData(null);
                mRewardMiningAdapter.setEmptyView(
                        EmptyViewUtil.setEmpty(recyclerView2, this, "暂无数据", 100));
            }
        }
    }
}

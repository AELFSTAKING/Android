package io.alf.exchange.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import io.alf.exchange.R;
import io.tick.base.mvp.MvpActivity;

public class RewardRuleActivity extends MvpActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void initPresenter() {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("规则");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reward_rule;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initEvents() {

    }
}

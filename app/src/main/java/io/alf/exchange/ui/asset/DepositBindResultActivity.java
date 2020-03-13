package io.alf.exchange.ui.asset;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.tick.base.mvp.BaseActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.RxBindingUtils;

public class DepositBindResultActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;

    private static final String CURRENCY_BEAN = "currencyBean";
    private CurrencyBean.ListBean currencyBean;

    public static void startUp(Context context, CurrencyBean.ListBean bean) {
        ActivityStartUtils.jump(context, DepositBindResultActivity.class,
                intent -> intent.putExtra(CURRENCY_BEAN, bean));
    }

    private CurrencyBean.ListBean getCurrencyBean() {
        if (currencyBean == null) {
            currencyBean = (CurrencyBean.ListBean) getIntent().getSerializableExtra(
                    CURRENCY_BEAN);
        }
        return currencyBean;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_deposit_bind;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("绑定成功");
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
        }, tvSubmit);
    }
}

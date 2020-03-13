package io.alf.exchange.ui.asset;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.mvp.presenter.BindAddressPresenter;
import io.alf.exchange.mvp.view.BindAddressView;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.Validator;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.RxBindingUtils;

public class DepositBindActivity extends MvpActivity implements BindAddressView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_address_title)
    TextView tvAddressTitle;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.et_note_name)
    EditText etNoteName;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.tv_notice)
    TextView tvNotice;

    private static final String CURRENCY_BEAN = "currencyBean";
    private CurrencyBean.ListBean currencyBean;

    private BindAddressPresenter mBindAddressPresenter;

    public static void startUp(Context context, CurrencyBean.ListBean bean) {
        ActivityStartUtils.jump(context, DepositBindActivity.class,
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
    protected void initPresenter() {
        mBindAddressPresenter = registerPresenter(new BindAddressPresenter(), this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_deposit_bind;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText(getCurrencyBean().alias + "跨链充值");
        tvAddressTitle.setText(
                String.format("%s原链地址", StringUtils.toUpperCase(getCurrencyBean().chain)));
        etAddress.setHint(
                String.format("请输入或粘贴%s地址", StringUtils.toUpperCase(getCurrencyBean().chain)));
        tvNotice.setText(String.format("%s原链充值地址将与%s地址: %s进行绑定，在完成跨链充值交易确认后，%s余额增加。",
                StringUtils.toUpperCase(getCurrencyBean().chain),
                StringUtils.toUpperCase(getCurrencyBean().tokenCurrency),
                WalletUtils.getAddress(),
                StringUtils.toUpperCase(getCurrencyBean().tokenCurrency)));
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            if (validate()) {
                String address = WalletUtils.getAddress();
                if (!StringUtils.isEmpty(address)) {
                    mBindAddressPresenter.bindAddress(
                            address,
                            etNoteName.getText().toString(),
                            getCurrencyBean().chain,
                            getCurrencyBean().currency,
                            etAddress.getText().toString());
                }
            }
        }, tvSubmit);
    }

    private boolean validate() {
        return Validator.checkAddress(this, etAddress)
                && Validator.checkAddress(this, etNoteName);
    }

    @Override
    public void onBindAddress(boolean success) {
        if (success) {
            finish();
        }
    }
}

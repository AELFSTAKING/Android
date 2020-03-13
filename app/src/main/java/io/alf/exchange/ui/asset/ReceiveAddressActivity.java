package io.alf.exchange.ui.asset;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.util.RxQRCode;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.CopyUtil;
import io.tick.base.util.DensityUtil;
import io.tick.base.util.RxBindingUtils;

public class ReceiveAddressActivity extends MvpActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_copy)
    TextView tvCopy;
    @BindView(R.id.tv_notice_1)
    TextView tvNotice1;

    private static final String CURRENCY_BEAN = "currencyBean";
    private CurrencyBean.ListBean currencyBean;

    public static void startUp(Context context, CurrencyBean.ListBean bean) {
        ActivityStartUtils.jump(context, ReceiveAddressActivity.class,
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

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_receive_addresss;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText(getCurrencyBean().alias + "收款");
        if (StringUtils.equalsIgnoreCase("PLANET", getCurrencyBean().alias)) {
            tvNotice1.setText(String.format("1、该地址只支持%s收款！", getCurrencyBean().alias));
        } else {
            tvNotice1.setText(
                    String.format("1、该地址只支持%s收款！请不要向本地址直接发起%s转账！", getCurrencyBean().alias,
                            getCurrencyBean().currency));
        }
        tvAddress.setText(WalletUtils.getAddress());
        ivCode.setImageBitmap(
                RxQRCode.createQRCode(WalletUtils.getAddress(), DensityUtil.dp2px(200),
                        DensityUtil.dp2px(200)));
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            CopyUtil.copy(this, "address", WalletUtils.getAddress());
            toast("复制成功");
        }, tvCopy);

    }
}

package io.alf.exchange.ui.asset;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import io.alf.exchange.R;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.CopyUtil;
import io.tick.base.util.RxBindingUtils;
import io.tick.base.util.ToastUtils;

public class ExportAccountActivity extends MvpActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_private_key)
    TextView tvPrivateKey;
    @BindView(R.id.iv_copy_address)
    ImageView ivCopyAddress;
    @BindView(R.id.iv_copy_private_key)
    ImageView ivCopyPrivateKey;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;


    private static final String ADDRESS = "address";
    private static final String PRIVATE_KEY = "privateKey";

    private String address;
    private String privateKey;

    public static void startUp(Context context, String address, String privateKey) {
        ActivityStartUtils.jump(context, ExportAccountActivity.class, intent -> {
            intent.putExtra(ADDRESS, address);
            intent.putExtra(PRIVATE_KEY, privateKey);
        });
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_export_account;
    }

    private String getAddress() {
        return address = getIntent().getStringExtra(ADDRESS);
    }

    private String getPrivateKey() {
        return privateKey = getIntent().getStringExtra(PRIVATE_KEY);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("导出ETH私钥");
        tvAddress.setText(getAddress());
        tvPrivateKey.setText(getPrivateKey());
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            CopyUtil.copy(this, "address", tvAddress.getText().toString());
            ToastUtils.showShortToast("复制成功");
        }, ivCopyAddress);
        RxBindingUtils.clicks(v -> {
            CopyUtil.copy(this, "privateKey", tvPrivateKey.getText().toString());
            ToastUtils.showShortToast("复制成功");
        }, ivCopyPrivateKey);
        RxBindingUtils.clicks(v -> finish(), tvSubmit);
    }
}

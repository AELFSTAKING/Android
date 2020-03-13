package io.alf.exchange.ui.asset;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import org.consenlabs.tokencore.wallet.Identity;
import org.consenlabs.tokencore.wallet.model.Metadata;
import org.consenlabs.tokencore.wallet.model.Network;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.presenter.RegisterAddressPresenter;
import io.alf.exchange.mvp.view.RegisterAddressView;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.Validator;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.RxBindingUtils;

public class CreateAccountActivity extends MvpActivity implements RegisterAddressView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_account_name)
    EditText etAccountName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;

    private RegisterAddressPresenter mRegisterAddressPresenter;
    private String password;

    @Override
    protected void initPresenter() {
        mRegisterAddressPresenter = registerPresenter(new RegisterAddressPresenter(), this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_account;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("创建充值帐户");
    }

    private boolean validate() {
        return Validator.checkAccountName(this, etAccountName)
                && Validator.checkNewPassword(this, etPassword, etConfirmPassword);
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            if (validate()) {
                createAccount();
            }
        }, tvSubmit);
    }

    private synchronized void createAccount() {
        String accountName = etAccountName.getText().toString();
        password = etPassword.getText().toString();
        Identity.createIdentity(
                accountName,
                password,
                "",
                Network.MAINNET,
                Metadata.P2WPKH,
                true);
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address)) {
            mRegisterAddressPresenter.registerAddress(address);
        }
    }

    @Override
    public void onRegisterAddress(String data) {
        toast("注册地址成功");
        String address = WalletUtils.getAddress();
        String prikey = WalletUtils.getPrivateKey(password);
        ExportAccountActivity.startUp(this, address, prikey);
        finish();
    }
}

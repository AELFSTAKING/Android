package io.alf.exchange.ui.asset;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import org.consenlabs.tokencore.wallet.Identity;
import org.consenlabs.tokencore.wallet.Wallet;
import org.consenlabs.tokencore.wallet.model.Metadata;
import org.consenlabs.tokencore.wallet.model.Network;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.presenter.RegisterAddressPresenter;
import io.alf.exchange.mvp.view.RegisterAddressView;
import io.alf.exchange.util.Validator;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.RxBindingUtils;

public class ImportAccountActivity extends MvpActivity implements RegisterAddressView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_private_key)
    EditText etPrivateKey;
    @BindView(R.id.et_account_name)
    EditText etAccountName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;

    private RegisterAddressPresenter mRegisterAddressPresenter;
    private Wallet wallet;
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
        return R.layout.activity_import_account;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("导入ETH账户");
    }

    private boolean validate() {
        return Validator.checkPrivateKey(this, etPrivateKey)
                && Validator.checkAccountName(this, etAccountName)
                && Validator.checkNewPassword(this, etPassword, etConfirmPassword);
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            if (validate()) {
                importAccount();
            }
        }, tvSubmit);
    }

    private synchronized void importAccount() {
        String privateKey = etPrivateKey.getText().toString();
        String accountName = etAccountName.getText().toString();
        password = etPassword.getText().toString();
        Identity.createIdentity(
                accountName,
                password,
                "",
                Network.MAINNET,
                Metadata.P2WPKH,
                false);
        wallet = WalletUtils.importEthWallet(privateKey, password);
        toast("账户导入成功, 正在注册地址，请稍等...");
        mRegisterAddressPresenter.registerAddress(wallet.getAddress());
    }

    @Override
    public void onRegisterAddress(String data) {
        toast("注册地址成功");
        ExportAccountActivity.startUp(this, wallet.getAddress(), wallet.exportPrivateKey(password));
        finish();
    }
}

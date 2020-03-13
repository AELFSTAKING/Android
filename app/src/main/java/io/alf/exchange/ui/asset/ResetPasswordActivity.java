package io.alf.exchange.ui.asset;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import org.consenlabs.tokencore.wallet.Identity;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.presenter.IdentityPresenter;
import io.alf.exchange.util.Validator;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.IView;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.RxBindingUtils;

public class ResetPasswordActivity extends MvpActivity implements IView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;

    private IdentityPresenter mIdentityPresenter;

    @Override
    protected void initPresenter() {
        mIdentityPresenter = registerPresenter(new IdentityPresenter(), this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("重置账户密码");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Identity identity = mIdentityPresenter.getIdentity();
        tvAccountName.setText(identity.getMetadata().getName());
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            if (validate()) {
                boolean ret = WalletUtils.changePassword(etOldPassword.getText().toString(),
                        etNewPassword.getText().toString());
                if (ret) {
                    toast("重置账户密码成功");
                    finish();
                } else {
                    toast("重置账户密码失败");
                }
            }
        }, tvSubmit);
    }

    private boolean validate() {
        return Validator.checkOldPassword(this, etOldPassword)
                && Validator.checkNewPassword(this, etNewPassword, etConfirmPassword);
    }
}

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
import io.tick.base.mvp.IView;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.RxBindingUtils;

public class ModifyAccountActivity extends MvpActivity implements IView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_old_name)
    TextView tvOldName;
    @BindView(R.id.et_new_name)
    EditText etNewName;
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
        return R.layout.activity_modify_account;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("修改账户名称");
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            if (validate()) {
                Identity identity = mIdentityPresenter.getIdentity();
                identity.setAccountName(etNewName.getText().toString());
                toast("账号名修改成功");
                finish();
            }
        }, tvSubmit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Identity identity = mIdentityPresenter.getIdentity();
        tvOldName.setText(identity.getMetadata().getName());
    }

    private boolean validate() {
        return Validator.checkAccountName(this, etNewName);
    }
}

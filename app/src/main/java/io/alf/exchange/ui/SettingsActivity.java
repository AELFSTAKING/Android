package io.alf.exchange.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import org.consenlabs.tokencore.wallet.Identity;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.dialog.PasswordDialog;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.Validator;
import io.tick.base.ContextProvider;
import io.tick.base.mvp.BaseActivity;
import io.tick.base.util.RxBindingUtils;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_quit)
    TextView tvQuit;

    private PasswordDialog mDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("设置");
        tvQuit.setVisibility(Identity.getCurrentIdentity() != null ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            mDialog = new PasswordDialog(SettingsActivity.this, v1 -> {
                TextView passwordView = (TextView) v1;
                if (Validator.checkPassword(SettingsActivity.this, passwordView)) {
                    try {
                        CexDataPersistenceUtils.clear();
                        Identity.getCurrentIdentity().deleteIdentity(
                                passwordView.getText().toString());
                        mDialog.dismiss();
                        restartApp();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mDialog.show();
        }, tvQuit);
    }

    public void restartApp() {
        final Intent intent = ContextProvider.getApp().getPackageManager()
                .getLaunchIntentForPackage(ContextProvider.getApp().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ContextProvider.getApp().startActivity(intent);
    }
}

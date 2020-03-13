package io.alf.exchange.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.alf.exchange.R;


public class PasswordDialog extends BaseDialog<PasswordDialog> {

    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    private View.OnClickListener confirmLister;

    public PasswordDialog(Context context, View.OnClickListener passwordConfirmLister) {
        super(context);
        this.confirmLister = passwordConfirmLister;
    }

    @Override
    public View onCreateView() {
        widthScale(0.8f);
        View inflate = View.inflate(mContext, R.layout.dialog_password, null);
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void setUiBeforShow() {
        tvCancel.setOnClickListener(v -> dismiss());
        tvConfirm.setOnClickListener(v -> {
            if (confirmLister != null) {
                confirmLister.onClick(etPassword);
            }
        });
    }
}

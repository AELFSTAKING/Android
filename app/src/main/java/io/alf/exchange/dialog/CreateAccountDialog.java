package io.alf.exchange.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.alf.exchange.R;


public class CreateAccountDialog extends BaseDialog<CreateAccountDialog> {

    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    private View.OnClickListener confirmLister;

    public CreateAccountDialog(Context context, View.OnClickListener confirmLister) {
        super(context);
        this.confirmLister = confirmLister;
    }

    @Override
    public View onCreateView() {
        widthScale(0.8f);
        View inflate = View.inflate(mContext, R.layout.dialog_create_account, null);
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void setUiBeforShow() {
        tvConfirm.setText("创建/导入充值账户");
        tvCancel.setOnClickListener(v -> dismiss());
        tvConfirm.setOnClickListener(confirmLister);
    }
}

package io.alf.exchange.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.alf.exchange.R;


public class DeleteAddressDialog extends BaseDialog<DeleteAddressDialog> {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    private View.OnClickListener confirmLister;

    public DeleteAddressDialog(Context context, View.OnClickListener confirmLister) {
        super(context);
        this.confirmLister = confirmLister;
    }

    @Override
    public View onCreateView() {
        widthScale(0.6f);
        View inflate = View.inflate(mContext, R.layout.dialog_delete_address, null);
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
        tvConfirm.setOnClickListener(confirmLister);
    }
}

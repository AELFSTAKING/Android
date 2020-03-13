package io.alf.exchange.dialog;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.flyco.animation.Attention.Swing;
import com.flyco.dialog.widget.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.alf.exchange.R;


public class ErrorDialog extends BaseDialog<ErrorDialog> {

    @BindView(R.id.tv_message)
    TextView tvMessage;

    private String message;

    public ErrorDialog(Context context, String messgea) {
        super(context);
        this.message = messgea;
    }

    @Override
    public View onCreateView() {
        widthScale(0.6f);
        showAnim(new Swing());
        View inflate = View.inflate(mContext, R.layout.dialog_error, null);
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void show() {
        super.show();
        new Handler().postDelayed(this::dismiss, 2000);
    }

    @Override
    public void setUiBeforShow() {
        tvMessage.setText(message);
    }
}

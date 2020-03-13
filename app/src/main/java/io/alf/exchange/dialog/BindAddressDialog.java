package io.alf.exchange.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.util.StringUtils;


public class BindAddressDialog extends BaseDialog<BindAddressDialog> {

    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    private View.OnClickListener confirmLister;
    private CurrencyBean.ListBean currencyBean;

    public BindAddressDialog(Context context, CurrencyBean.ListBean currencyBean,
            View.OnClickListener confirmLister) {
        super(context);
        this.currencyBean = currencyBean;
        this.confirmLister = confirmLister;
    }

    @Override
    public View onCreateView() {
        widthScale(0.8f);
        View inflate = View.inflate(mContext, R.layout.dialog_bind_address, null);
        ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void setUiBeforShow() {
        tvMessage.setText(String.format("请充值先绑定%s账户", getCurrencyAlias()));
        tvConfirm.setText(String.format("绑定%s账户", getCurrencyAlias()));
        tvCancel.setOnClickListener(v -> dismiss());
        tvConfirm.setOnClickListener(confirmLister);
    }

    private String getCurrencyAlias() {
        if (currencyBean != null) {
            return StringUtils.toUpperCase(currencyBean.alias);
        } else {
            return "";
        }
    }
}

package io.tick.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class LoadingDialog extends ProgressDialog {

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.base_dialog_loading, null);
        setCanceledOnTouchOutside(true);
        setContentView(contentView);
    }
}

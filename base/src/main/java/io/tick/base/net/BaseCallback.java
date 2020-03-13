package io.tick.base.net;

import com.lzy.okgo.model.Response;

import io.tick.base.mvp.IView;
import io.tick.base.net.callbck.JsonCallback;


public class BaseCallback<T> extends JsonCallback<T> {

    private boolean showBizError = true;
    private boolean showLoading = true;
    private IView view;

    public BaseCallback(String url) {
        super(url);
    }

    public BaseCallback(String url, IView view) {
        this(url, view, true);
    }

    public BaseCallback(String url, IView view, boolean showLoading) {
        super(url);
        this.view = view;
        this.showLoading = showLoading;
        if (view != null && this.showLoading) {
            view.showLoading();
        }
    }

    @Override
    public void onSuccess(Response<T> response) {
        if (view != null && this.showLoading) {
            view.hideLoading();
        }
        super.onSuccess(response);
    }

    @Override
    public void onError(Response<T> response) {
        if (view != null && this.showLoading) {
            view.hideLoading();
        }
        super.onError(response);
    }
}

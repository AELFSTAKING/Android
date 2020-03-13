package io.tick.base.net.callbck;

import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.callback.AbsCallback;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import io.tick.base.BaseApp;
import io.tick.base.R;
import io.tick.base.util.ToastUtils;
import okhttp3.Response;

public abstract class JsonCallback<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;
    private String url;

    public JsonCallback(String url) {
        this.url = url;
    }

    public JsonCallback(Type type) {
        this.type = type;
    }

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }


    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     */
    @Override
    public T convertResponse(Response response) throws Throwable {
        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
                return convert.convertResponse(response);
            }
        }

        JsonConvert<T> convert = new JsonConvert<>(type);
        return convert.convertResponse(response);
    }

    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        super.onError(response);
        // 上报网络异常到bugly服务器。
        postBugly(response.getException());
        int code = response.code();
        if (code == 404) {
            ToastUtils.showShortToast(R.string.b_net_url_error);
        }
        if (response.getException() instanceof SocketTimeoutException) {
            ToastUtils.showShortToast(R.string.b_net_socket_time_out);
        } else if (response.getException() instanceof SocketException) {
            ToastUtils.showShortToast(R.string.b_net_socket_exception);
        } else if (response.getException() instanceof JsonSyntaxException) {
            ToastUtils.showShortToast(R.string.b_net_error_parse);
        }
    }

    protected String validateError(com.lzy.okgo.model.Response<T> response) {
        // 上报网络异常到bugly服务器。
        postBugly(response.getException());
        if (response.getException() instanceof SocketTimeoutException) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_socket_time_out);
        } else if (response.getException() instanceof SocketException) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_socket_exception);
        } else if (response.getException() instanceof JsonSyntaxException) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_error_parse);
        }
        int code = response.code();
        if (code == 404) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_url_error);
        }
        if (code >= 500) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_server_error);
        } else if (code >= 400) {
            return BaseApp.getContext().getResources().getString(R.string.b_net_api_error);
        } else {
            return BaseApp.getContext().getResources().getString(R.string.b_net_unknown_error,
                    code);
        }
    }

    private void postBugly(Throwable throwable) {
        if (throwable != null) {
            CrashReport.setUserSceneTag(BaseApp.getContext(), 10000);
            CrashReport.putUserData(BaseApp.getContext(), "url", url);
            CrashReport.postCatchedException(throwable);
        }
    }
}

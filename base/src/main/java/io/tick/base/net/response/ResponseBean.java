package io.tick.base.net.response;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.tick.base.util.ToastUtils;

public class ResponseBean<T> implements Serializable {

    @SerializedName(value = "respCode", alternate = "code")
    public String code;
    @SerializedName("msg")
    public String message;
    public T data = null;

    public boolean isSuccess() {
        if (!"000000".equals(code)) {
            Log.i("Tick", code + ": " + message);
        }
        return "000000".equals(code);
    }

    public void showErrorMsg() {
        if (!isSuccess()) {
            ToastUtils.showShortToast(message);
        }
    }
}
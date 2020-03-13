package io.tick.base.appupdate;

import android.util.Log;

import androidx.annotation.NonNull;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.vector.update_app.HttpManager;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.tick.base.net.HttpUtils;
import io.tick.base.net.callbck.JsonCallback;
import io.tick.base.net.response.ResponseBean;
import io.tick.base.util.DeviceUtils;
import io.tick.base.util.JsonUtils;
import io.tick.base.util.LanguageHelper;

public class UpdateManager implements HttpManager {

    public UpdateManager() {
    }

    /**
     * 异步get
     *
     * @param url      get请求地址
     * @param params   get参数
     * @param callBack 回调
     */
    @Override
    public void asyncGet(@NonNull String url, @NonNull final Map<String, String> params,
            @NonNull final Callback callBack) {
        HttpUtils.getRequest(url, "", params, new JsonCallback<ResponseBean<AppUpdateBean>>(url) {
            @Override
            public void onSuccess(Response<ResponseBean<AppUpdateBean>> response) {
                super.onSuccess(response);
                if (response.isSuccessful()) {
                    AppUpdateBean bean = response.body().data;
                    if (bean != null && bean.isUpdate() && bean.isForcedUpdate()) {
                        callBack.onResponse(JsonUtils.toJsonString(bean));
                    }
                }
            }

            @Override
            public void onError(Response<ResponseBean<AppUpdateBean>> response) {
                callBack.onError(validateError(response));
            }
        });
    }

    /**
     * 异步post
     *
     * @param url      post请求地址
     * @param params   post请求参数
     * @param callBack 回调
     */
    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params,
            @NonNull final Callback callBack) {
        HttpUtils.postRequest(url, "", generateRequestHeader(), generateRequestBody(params),
                new JsonCallback<ResponseBean<AppUpdateBean>>(url) {
                    @Override
                    public void onSuccess(Response<ResponseBean<AppUpdateBean>> response) {
                        super.onSuccess(response);
                        if (response.isSuccessful()) {
                            AppUpdateBean bean = response.body().data;
                            if (bean != null && bean.isUpdate()) {
                                callBack.onResponse(JsonUtils.toJsonString(bean));
                            }
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<AppUpdateBean>> response) {
                        super.onError(response);
                        callBack.onError(validateError(response));
                    }
                });
    }

    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param fileName 文件名称
     * @param callback 回调
     */
    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName,
            @NonNull final FileCallback callback) {
        HttpUtils.fileDownload(url, new MFileCallback(path, fileName) {
            @Override
            public void onSuccess(Response<File> response) {
                callback.onResponse(response.body());
            }

            @Override
            public void onStart(Request<File, ? extends Request> request) {
                super.onStart(request);
                callback.onBefore();
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                callback.onError(validateError(response));
            }

            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                Log.i("Tick", "speed : " + progress.speed + ", fraction : " + progress.fraction
                        + ", currentSize : " + progress.currentSize + ", totalSize : "
                        + progress.totalSize);
                callback.onProgress(progress.fraction, progress.totalSize);
            }
        });
    }

    protected HttpHeaders generateRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("DEVICEID", DeviceUtils.getClientId());
        headers.put("DEVICESOURCE", "native");
        headers.put("Lang", LanguageHelper.getLang());
        //String token = SpHelper.getObj(BConstant.SP_STRING_TOKEN, String.class);
        //if (!TextUtils.isEmpty(token)) {
        //    headers.put("CEXTOKEN", token);
        //}
        return headers;
    }

    protected Map<String, Object> generateRequestBody(Map data) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("lang", LanguageHelper.getLang());
        if (data != null) {
            body.put("data", new JSONObject(data));
        }
        return body;
    }
}
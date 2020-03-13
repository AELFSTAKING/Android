package io.tick.base.net;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpHeaders;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import io.tick.base.net.callbck.JsonCallback;

public class HttpUtils {
    /**
     * Gets requets.
     *
     * @param <T>      the type parameter
     * @param url      the url
     * @param tag      the tag
     * @param params   the params
     * @param callback the callback
     */
    public static <T> void getRequest(String url, Object tag, Map<String, String> params,
            JsonCallback<T> callback) {
        OkGo.<T>get(url)
                .tag(tag)
                .params(params)
                .execute(callback);
    }

    /**
     * Post request.
     */
    public static <T> void postRequest(String url, Object tag, HttpHeaders headers,
            Map<String, Object> params, JsonCallback<T> callback) {
        OkGo.<T>post(url)
                .tag(tag)
                .headers(headers)
                .upJson(new JSONObject(params))
                .execute(callback);
    }

    /**
     * Post request.
     *
     * @param <T>      the type parameter
     * @param url      the url
     * @param tag      the tag
     * @param params   the params
     * @param callback the callback
     */
    public static <T> void postRequest(String url, Object tag, Map<String, Object> params,
            JsonCallback<T> callback) {
        OkGo.<T>post(url)
                .tag(tag)
                .upJson(new JSONObject(params))
                .execute(callback);
    }

    /**
     * Post request.
     *
     * @param <T>      the type parameter
     * @param url      the url
     * @param tag      the tag
     * @param callback the callback
     */
    public static <T> void postRequest(String url, Object tag, JsonCallback<T> callback) {
        OkGo.<T>post(url)
                .tag(tag)
                .upJson("{}")
                .execute(callback);
    }

    /**
     * File download request.
     *
     * @param url      the url
     * @param callback the callback
     */
    public static void fileDownload(String url, FileCallback callback) {
        OkGo.<File>get(url)
                .execute(callback);
    }


    /**
     * cancel all request with tag
     *
     * @param tag the tag
     */
    public static void cancelTag(Object tag) {
        OkGo.getInstance().cancelTag(tag);
    }
}

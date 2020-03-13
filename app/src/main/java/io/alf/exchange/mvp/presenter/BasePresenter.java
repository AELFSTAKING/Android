package io.alf.exchange.mvp.presenter;

import android.text.TextUtils;

import com.lzy.okgo.model.HttpHeaders;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.tick.base.util.LanguageHelper;
import io.tick.base.mvp.IView;
import io.tick.base.mvp.Presenter;
import io.tick.base.util.DeviceUtils;


public class BasePresenter<T extends IView> extends Presenter<T> {

    protected HttpHeaders generateRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("DEVICEID", DeviceUtils.getClientId());
        headers.put("DEVICESOURCE", "native");
        headers.put("Lang", LanguageHelper.getLang());
        HttpHeaders.setAcceptLanguage(LanguageHelper.getLang());
/*        String token = DataPersistenceUtils.getToken();
        if (!TextUtils.isEmpty(token)) {
            headers.put("CEXTOKEN", token);
        }*/
        return headers;
    }

    protected HttpHeaders generateRequestHeader(String channelNo) {
        HttpHeaders headers = generateRequestHeader();
/*        ChannelToken token = CexDataPersistenceUtils.getChannelToken(channelNo);
        if (token != null && !TextUtils.isEmpty(token.channelToken)) {
            headers.put("CEXPASSPORT", token.channelToken);
        }*/
        return headers;
    }

    protected HttpHeaders generateGetRequestHeader() {
        HttpHeaders headers = generateRequestHeader();
        headers.put("Lang", LanguageHelper.getLang());
        HttpHeaders.setAcceptLanguage(LanguageHelper.getLang());
        headers.put("Content-Type", "application/json;charset=UTF-8");
        return headers;
    }

    protected HttpHeaders generateGetRequestHeader(String channelNo) {
        HttpHeaders headers = generateGetRequestHeader();
/*        ChannelToken token = CexDataPersistenceUtils.getChannelToken(channelNo);
        if (token != null && !TextUtils.isEmpty(token.channelToken)) {
            headers.put("CEXPASSPORT", token.channelToken);
        }*/
        return headers;
    }

    protected Map<String, Object> generateRequestBody(Map<String, Object> data) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("lang", LanguageHelper.getLang());
        if (data != null && data.size() > 0) {
            body.put("data", new JSONObject(data));
        }
        return body;
    }

    protected Map<String, Object> generateRequestBody() {
        return generateRequestBody("");
    }

    protected Map<String, Object> generateRequestBody(String data) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("lang", LanguageHelper.getLang());
        if (!TextUtils.isEmpty(data)) {
            body.put("data", data);
        }
        return body;
    }
}

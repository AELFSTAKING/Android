package io.alf.exchange.mvp.bean;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.Objects;

public class MqttConfigBean implements Serializable {
    // 订阅账号
    public String subscriber;
    public String mqttHost;
    // wss订阅端口
    public String wssPort;
    // 订阅密码
    public String subscriberPwd;
    // 顶层Topic
    public String topicPrefix;
    // tcp订阅端口
    public String tcpPort;
    // 是否显示
    public String isShow;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MqttConfigBean)) return false;
        MqttConfigBean that = (MqttConfigBean) o;
        return TextUtils.equals(subscriber, that.subscriber) &&
                TextUtils.equals(mqttHost, that.mqttHost) &&
                TextUtils.equals(wssPort, that.wssPort) &&
                TextUtils.equals(subscriberPwd, that.subscriberPwd) &&
                TextUtils.equals(topicPrefix, that.topicPrefix) &&
                TextUtils.equals(tcpPort, that.tcpPort) &&
                TextUtils.equals(isShow, that.isShow);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(subscriber, mqttHost, wssPort, subscriberPwd, topicPrefix, tcpPort, isShow);
    }
}

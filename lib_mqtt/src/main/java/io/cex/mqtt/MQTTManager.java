package io.cex.mqtt;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;
import java.util.HashSet;

import io.tick.base.util.DeviceUtils;

public class MQTTManager {

    private static final String TAG = MQTTManager.class.getSimpleName();

    private MqttAsyncClient mClient;
    private MqttConnectOptions connectOptions;
    private boolean inited = false;
    private HashSet mTopics = new HashSet();
    private MQTTStateManager mqttStateManager = MQTTStateManager.getInstance();
    private IMqttActionListener actionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            mqttStateManager.onSuccess(asyncActionToken);
            //链接成功，或者重新链接成功都将在connectComplete中处理
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            mqttStateManager.onFailure(asyncActionToken, exception);
            //reConnect();
        }
    };
    private IMqttMessageListener mIMqttMessageListener = (topic, message) -> {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, " IMqttMessageListener messageArrived: " + topic + ", " + message.toString());
        }
        handleMassage(topic, message);
    };
    private MqttCallbackExtended callback = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            mqttStateManager.connectComplete(reconnect, serverURI);
            //链接完成，订阅所有的topic
            subscribeDefaultTopic((String[]) mTopics.toArray(new String[0]));
        }

        @Override
        public void connectionLost(Throwable cause) {
            mqttStateManager.connectionLost(cause);
            //reConnect();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            mqttStateManager.messageArrived(topic, message);
            // 消息偶尔会到这里, 处理数据
            handleMassage(topic, message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            mqttStateManager.deliveryComplete(token);
            // publish后会执行到这里
        }
    };

    private MQTTManager() {

    }

    public static MQTTManager getInstance() {
        return Holder.instance;
    }

    public void disconnect() {
        if (mClient != null) {
            synchronized (MQTTManager.class) {
                try {
                    Log.i(TAG, "Disconnect MQTTManager Start");
                    mClient.disconnect();
                    Log.i(TAG, "Disconnect MQTTManager Success");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                mClient = null;
            }
        }
    }

    public boolean isInited() {
        return inited;
    }

    public void init(String subscriber, String subscriberPwd, String mqttHost, String mqttPort, String topicPrefix) {
        if (mClient == null) {
            synchronized (MQTTManager.class) {
                if (mClient == null) {
                    try {
                        if (!TextUtils.isEmpty(mqttHost)) mqttHost = mqttHost.trim();
                        if (!TextUtils.isEmpty(mqttPort)) mqttPort = mqttPort.trim();
                        String url = "tcp://".concat(mqttHost).concat(":").concat(mqttPort);
                        mClient = new MqttAsyncClient(url, DeviceUtils.getClientId() + "_" + System.currentTimeMillis(), new MemoryPersistence());
                        connectOptions = new MqttConnectOptions();
                        connectOptions.setUserName(subscriber);
                        connectOptions.setPassword(subscriberPwd.toCharArray());
                        connectOptions.setConnectionTimeout(10);  //超时时间
                        connectOptions.setKeepAliveInterval(10); //心跳时间,单位秒
                        connectOptions.setAutomaticReconnect(true);//自动重连
                        connectOptions.setCleanSession(true);      // 清除缓存
                        mClient.setCallback(callback);
                        initTopic(topicPrefix);
                        mClient.connect(connectOptions, null, actionListener);
                        inited = true;
                        Log.i(TAG, "Init MQTTManager Success");
                    } catch (Exception e) {
                        Log.e(TAG, "Init MQTTManager Failed", e);
                        CrashReport.postCatchedException(e);
                        mClient = null;
                    }
                }
            }
        }
    }

    private void initTopic(String topicPrefix) {
        // 分区行情
        //mTopics.add(topicPrefix + MQTTTopic.getQuotationArea());
        // 分组行情
        //mTopics.add(topicPrefix + MQTTTopic.getQuotationGroup());
        // 热门交易对数据
        //mTopics.add(topicPrefix + MQTTTopic.getHotSymbols());
        // 涨幅交易对
        mTopics.add(topicPrefix + MQTTTopic.getIncreaseSymbols());
    }

    public void reConnect() {
        if (mClient != null && !mClient.isConnected() && connectOptions != null) {
            try {
                mClient.connect(connectOptions, null, actionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void subscribeDefaultTopic(String[] topics) {
        if (topics.length > 0) {
            subscribe(topics);
        }
    }

    // qos:0至多一次发完即丢弃<=1
    // qos:1至少一次需要确认回复>=1
    // qos:2只有一次需要确认回复=1
    // qos:3待用，保留位置
    public void subscribe(String... topics) {
        try {
            Log.d(TAG, "subscribe topic start -->: " + Arrays.toString(topics));
            if (null != mClient && mClient.isConnected() && topics != null && topics.length > 0) {
                for (String topic : topics) {
                    mTopics.add(topic);
                }
                mClient.subscribe(topics, new int[topics.length], getMessageListeners(topics.length));
                Log.d(TAG, "subscribe topic finished --> " + Arrays.toString(topics));
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            Log.d(TAG, "subscribe topic is error --> " + e);
        }
    }

    public void unsubscribe(String... topics) {
        try {
            if (null != mClient && mClient.isConnected() && topics != null && topics.length > 0) {
                for (String topic : topics) {
                    mTopics.remove(topic);
                }
                mClient.unsubscribe(topics);
                Log.d(TAG, "unsubscribe topic finished --> " + Arrays.toString(topics));
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
            Log.d(TAG, "unsubscribe topic is error --> " + e);
        }
    }

    private IMqttMessageListener[] getMessageListeners(int size) {
        IMqttMessageListener[] listeners = new IMqttMessageListener[size];
        Arrays.fill(listeners, mIMqttMessageListener);
        return listeners;
    }

    private void handleMassage(String topic, MqttMessage message) {
        if (!TextUtils.isEmpty(message.toString())) {
            ParserManager.getInstance().parse(topic, message.toString());
        }
    }

    private static class Holder {
        private static MQTTManager instance = new MQTTManager();
    }
}

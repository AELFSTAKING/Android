package io.cex.mqtt;


import static io.tick.base.eventbus.EventCode.MQTT_CONNECTION_STATUS;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.tick.base.eventbus.EventBusCenter;

public class MQTTStateManager implements MQTTStateListener {

    private static final String TAG = MQTTManager.class.getSimpleName();

    private static volatile MQTTStateManager sInstance;

    private StateBean stateBean;

    private MQTTStateManager() {
    }

    public static MQTTStateManager getInstance() {
        if (sInstance == null) {
            synchronized (MQTTStateManager.class) {
                if (sInstance == null) {
                    sInstance = new MQTTStateManager();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {

    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.e(TAG, "IMqttActionListener error --> ", exception);
        stateBean = new StateBean(false, exception);
        EventBusCenter.post(MQTT_CONNECTION_STATUS, stateBean);
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.d(TAG, "MqttCallbackExtended connectComplete: " + reconnect + ", " + serverURI);
        stateBean = new StateBean(true, reconnect, serverURI);
        EventBusCenter.post(MQTT_CONNECTION_STATUS, stateBean);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "MqttCallbackExtended connectionLost: ", cause);
        stateBean = new StateBean(false, cause);
        EventBusCenter.post(MQTT_CONNECTION_STATUS, stateBean);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.d(TAG, "MqttCallbackExtended messageArrived: " + topic + ", " + message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "MqttCallbackExtended deliveryComplete");
    }

    public StateBean getStateBean() {
        return stateBean;
    }

    public static class StateBean {
        public boolean connected;
        public boolean reconnect;
        public String serverURI;
        public Throwable cause;

        public StateBean(boolean connected, boolean reconnect, String serverURI) {
            this.connected = connected;
            this.reconnect = reconnect;
            this.serverURI = serverURI;
        }

        public StateBean(boolean connected, Throwable cause) {
            this.connected = connected;
            this.cause = cause;
        }
    }
}

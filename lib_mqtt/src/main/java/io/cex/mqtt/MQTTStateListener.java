package io.cex.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;

public interface MQTTStateListener extends MqttCallbackExtended, IMqttActionListener {
}

package io.alf.exchange;

import android.text.TextUtils;

import java.util.HashMap;

import io.alf.exchange.mvp.bean.MqttConfigBean;
import io.cex.mqtt.MQTTManager;
import io.cex.mqtt.MQTTService;
import io.cex.mqtt.MQTTTopic;


public class MqttTopicManager {

    private static HashMap<String, String> mTopics = new HashMap<>();
    private static String topicPrefix;
    private static String KLINE_TOPIC = "klineTopic";
    private static String ORDER_BOOK_TOPIC = "orderBookTopic";
    private static String DEPTH_TOPIC = "depthTopic";
    private static String DEAL_TOPIC = "depthTopic";
    private static String SYMBOL_QUOTATION = "symbolQuotation";

    public static void connectMqtt(MqttConfigBean bean) {
        // 1.先断开老连接
        MQTTService.disconnectMqtt();
        // 2.开始新连接
        MQTTService.start(
                App.getContext(),
                bean.subscriber,
                bean.subscriberPwd,
                bean.mqttHost,
                bean.tcpPort,
                bean.topicPrefix
        );
    }

    public static boolean isConnected() {
        return MQTTManager.getInstance().isInited();
    }

    /**
     * 币种最新深度图数据
     *
     * @param symbol 币种
     */
    public static void subscribeDepthData(String symbol) {
        String newTopic = MQTTTopic.getTransactionDepth(symbol);
        String oldTopic = mTopics.get(DEPTH_TOPIC);
        if (!TextUtils.equals(newTopic, oldTopic)) {
            mTopics.put(DEPTH_TOPIC, newTopic);
            if (!TextUtils.isEmpty(oldTopic)) {
                MQTTService.unsubscribe(App.getContext(), oldTopic);
            }
            MQTTService.subscribe(App.getContext(), newTopic);
        }
    }

    public static void subscribeOrderBookData(String symbol, String step) {
        String newTopic = MQTTTopic.getOrderBook(symbol, step);
        String oldTopic = mTopics.get(ORDER_BOOK_TOPIC);
        if (!TextUtils.equals(newTopic, oldTopic)) {
            mTopics.put(ORDER_BOOK_TOPIC, newTopic);
            if (!TextUtils.isEmpty(oldTopic)) {
                MQTTService.unsubscribe(App.getContext(), oldTopic);
            }
            MQTTService.subscribe(App.getContext(), newTopic);
        }
    }

    public static void subscribeKLineData(String symbol, String timeRange) {
        String newTopic = MQTTTopic.getTransactionKline(symbol, timeRange);
        String oldTopic = mTopics.get(KLINE_TOPIC);
        if (!TextUtils.equals(newTopic, oldTopic)) {
            mTopics.put(KLINE_TOPIC, newTopic);
            if (!TextUtils.isEmpty(oldTopic)) {
                MQTTService.unsubscribe(App.getContext(), oldTopic);
            }
            MQTTService.subscribe(App.getContext(), newTopic);
        }
    }

    /**
     * 币种最新成交列表
     *
     * @param symbol 币种
     */
    public static void subscribeDealData(String symbol) {
        String newTopic = MQTTTopic.getTransactionDeal(symbol);
        String oldTopic = mTopics.get(DEAL_TOPIC);
        if (!TextUtils.equals(newTopic, oldTopic)) {
            mTopics.put(DEAL_TOPIC, newTopic);
            if (!TextUtils.isEmpty(oldTopic)) {
                MQTTService.unsubscribe(App.getContext(), oldTopic);
            }
            MQTTService.subscribe(App.getContext(), newTopic);
        }
    }

    /**
     * 币种行情数据
     *
     * @param symbol 币种
     */
    public static void subscribeQuotationData(String symbol) {
        String newTopic = MQTTTopic.getSymbolQuotation(symbol);
        String oldTopic = mTopics.get(SYMBOL_QUOTATION);
        if (!TextUtils.equals(newTopic, oldTopic)) {
            mTopics.put(SYMBOL_QUOTATION, newTopic);
            if (!TextUtils.isEmpty(oldTopic)) {
                MQTTService.unsubscribe(App.getContext(), oldTopic);
            }
            MQTTService.subscribe(App.getContext(), newTopic);
        }
    }
}

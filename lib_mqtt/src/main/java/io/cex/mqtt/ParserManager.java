package io.cex.mqtt;

import static io.tick.base.eventbus.EventCode.MQTT_HOT_SYMBOLS;
import static io.tick.base.eventbus.EventCode.MQTT_INCREASE_SYMBOLS;
import static io.tick.base.eventbus.EventCode.MQTT_ORDER_BOOK;
import static io.tick.base.eventbus.EventCode.MQTT_QUOTATION_AREA;
import static io.tick.base.eventbus.EventCode.MQTT_QUOTATION_GROUP;
import static io.tick.base.eventbus.EventCode.MQTT_SYMBOL_QUOTATION;
import static io.tick.base.eventbus.EventCode.MQTT_TRANSACTION_DEAL;
import static io.tick.base.eventbus.EventCode.MQTT_TRANSACTION_DEPTH;
import static io.tick.base.eventbus.EventCode.MQTT_TRANSACTION_KLINE;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.List;

import io.cex.mqtt.bean.DealBean;
import io.cex.mqtt.bean.DepthBean;
import io.cex.mqtt.bean.HotSymbolList;
import io.cex.mqtt.bean.IncreaseSymbolsBean;
import io.cex.mqtt.bean.KLineBean;
import io.cex.mqtt.bean.OrderBookBean;
import io.cex.mqtt.bean.QuotationBean;
import io.cex.mqtt.bean.QuotationGroupBean;
import io.cex.mqtt.bean.QuotationGroupByAreaBean;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.net.response.ResponseBean;
import io.tick.base.util.JsonUtils;

/**
 * 数据解析管理类
 */
public class ParserManager {

    private static ObjectMapper mapper;

    private static volatile ParserManager sInstance;

    private ParserManager() {
    }

    public static ParserManager getInstance() {
        if (sInstance == null) {
            synchronized (ParserManager.class) {
                if (sInstance == null) {
                    sInstance = new ParserManager();
                }
            }
        }
        return sInstance;
    }

    private static ObjectMapper getObjectMapper() {
        if (mapper == null) {
            synchronized (JsonUtils.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                    // 在反序列化时忽略在 json 中存在但 Java 对象不存在的属性
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    // 在序列化时忽略值为 null 的属性
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                }
            }
        }
        return mapper;
    }

    /**
     * 解析mqtt返回的message
     *
     * @param topic   mqtt返回的的message的topic
     * @param message 需要解析的数据
     */
    public void parse(String topic, String message) {
        try {
            if (MQTTTopic.QUOTATION_GROUP.equalsIgnoreCase(topic)) {
                // 分组行情
                ResponseBean<List<QuotationGroupBean>> responseBean = getObjectMapper().readValue(
                        message, new TypeReference<ResponseBean<List<QuotationGroupBean>>>() {
                        });
                if (responseBean != null && responseBean.isSuccess()) {
                    EventBusCenter.post(MQTT_QUOTATION_GROUP, responseBean.data);
                }
            } else if (MQTTTopic.QUOTATION_AREA.equalsIgnoreCase(topic)) {
                // 分区行情
                ResponseBean<List<QuotationGroupByAreaBean>> responseBean =
                        getObjectMapper().readValue(message,
                                new TypeReference<ResponseBean<List<QuotationGroupByAreaBean>>>() {
                                });
                if (responseBean != null && responseBean.isSuccess()) {
                    EventBusCenter.post(MQTT_QUOTATION_AREA, responseBean.data);
                }
            } else if (topic.startsWith(MQTTTopic.TRANSACTION_DEAL)) {
                // 最新成交列表
                ResponseBean<List<DealBean>> responseBean = getObjectMapper().readValue(message,
                        new TypeReference<ResponseBean<List<DealBean>>>() {
                        });
                if (responseBean != null && responseBean.isSuccess()) {
                    EventBusCenter.post(MQTT_TRANSACTION_DEAL, responseBean.data);
                }
            } else if (topic.startsWith(MQTTTopic.TRANSACTION_KLINE)) {
                // K线数据
                ResponseBean<KLineBean> responseBean = getObjectMapper().readValue(message,
                        new TypeReference<ResponseBean<KLineBean>>() {
                        });
                if (responseBean != null && responseBean.isSuccess()) {
                    EventBusCenter.post(MQTT_TRANSACTION_KLINE, responseBean.data);
                }
            } else if (topic.startsWith(MQTTTopic.TRANSACTION_DEPTH)) {
                // 最新深度图数据
                ResponseBean<DepthBean> responseBean = getObjectMapper().readValue(message,
                        new TypeReference<ResponseBean<DepthBean>>() {
                        });
                if (responseBean != null && responseBean.isSuccess()) {
                    EventBusCenter.post(MQTT_TRANSACTION_DEPTH, responseBean.data);
                }
            } else if (topic.startsWith(MQTTTopic.ORDER_BOOK)) {
                // 订单簿数据
                ResponseBean<OrderBookBean> responseBean = getObjectMapper().readValue(message,
                        new TypeReference<ResponseBean<OrderBookBean>>() {
                        });
                if (responseBean != null && responseBean.isSuccess()) {
                    EventBusCenter.post(MQTT_ORDER_BOOK, responseBean.data);
                }
            } else if (topic.startsWith(MQTTTopic.HOT_SYMBOLS)) {
                // 热门交易对数据
                ResponseBean<HotSymbolList> responseBean = getObjectMapper().readValue(message,
                        new TypeReference<ResponseBean<HotSymbolList>>() {
                        });
                if (responseBean != null && responseBean.isSuccess()) {
                    EventBusCenter.post(MQTT_HOT_SYMBOLS, responseBean.data);
                }
            } else if (topic.startsWith(MQTTTopic.SYMBOL_QUOTATION)) {
                ResponseBean<QuotationBean> responseBean = getObjectMapper().readValue(message,
                        new TypeReference<ResponseBean<QuotationBean>>() {
                        });
                if (responseBean != null && responseBean.isSuccess()) {
                    EventBusCenter.post(MQTT_SYMBOL_QUOTATION, responseBean.data);
                }
            } else if (topic.startsWith(MQTTTopic.INCREASE_SYMBOLS)) {
                ResponseBean<IncreaseSymbolsBean> responseBean = getObjectMapper().readValue(
                        message,
                        new TypeReference<ResponseBean<IncreaseSymbolsBean>>() {
                        });
                if (responseBean != null && responseBean.isSuccess()) {
                    if (responseBean.data != null && responseBean.data.upList != null
                            && responseBean.data.upList.size() > 0) {
                        for (QuotationBean quotationBean : responseBean.data.upList) {
                            if (TextUtils.isEmpty(quotationBean.lastPrice) && !TextUtils.isEmpty(
                                    quotationBean.price)) {
                                quotationBean.lastPrice = quotationBean.price;
                            }
                            if (TextUtils.isEmpty(quotationBean.lastUsdPrice) && !TextUtils.isEmpty(
                                    quotationBean.usdPrice)) {
                                quotationBean.lastUsdPrice = quotationBean.usdPrice;
                            }
                        }
                    }
                    EventBusCenter.post(MQTT_INCREASE_SYMBOLS, responseBean.data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }
    }
}

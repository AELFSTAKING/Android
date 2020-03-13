package io.alf.exchange;

import io.alf.exchange.mvp.bean.MqttConfigBean;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.objectbox.BoxStore;
import io.tick.base.BaseApp;

public class App extends BaseApp {

    private static App mInstance;
    private static BoxStore mBoxStore;


    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initDB();
    }

    private void initDB() {
        //数据库
        mBoxStore = MyObjectBox.builder().androidContext(this).build();
    }

    public static BoxStore getBoxStore() {
        return mBoxStore;
    }

    private void initMqtt() {
        MqttConfigBean oldConfig = CexDataPersistenceUtils.getMqttConfig();
        if (oldConfig != null && !MqttTopicManager.isConnected()) {
            MqttTopicManager.connectMqtt(oldConfig);
        }
    }
}

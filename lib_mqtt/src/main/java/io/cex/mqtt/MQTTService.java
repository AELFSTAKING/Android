package io.cex.mqtt;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MQTTService extends IntentService {

    private static final String TAG = MQTTService.class.getSimpleName();

    private static final String SCHEME = "io.kofo.mqtt";

    private static final String ACTION_MQTT_INIT = SCHEME + ".action.mqtt.init";
    private static final String ACTION_SUBSCRIBE_TOPIC = SCHEME + ".action.subscribe";
    private static final String ACTION_UNSUBSCRIBE_TOPIC = SCHEME + ".action.unsubscribe";

    private static final String EXTRA_SUBSCRIBE_TOPIC = SCHEME + ".EXTRA.subscribe.topic";
    private static final String EXTRA_UNSUBSCRIBE_TOPIC = SCHEME + ".EXTRA.unsubscribe.topic";

    private static final String USER_NAME = "subscriber";
    private static final String PASSWORD = "subscriberPwd";
    private static final String HOST = "mqttHost";
    private static final String PORT = "mqttPort";
    private static final String TOPIC_PREFIX = "topicPrefix";

    public MQTTService() {
        super("MQTTService");
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MQTTService.class);
        context.stopService(intent);
    }

    public static void disconnectMqtt() {
        MQTTManager.getInstance().disconnect();
    }

    public static void start(Context context, String subscriber, String subscriberPwd, String mqttHost, String mqttPort, String topicPrefix) {
        Intent intent = new Intent(context, MQTTService.class);
        intent.setAction(ACTION_MQTT_INIT);
        intent.putExtra(USER_NAME, subscriber);
        intent.putExtra(PASSWORD, subscriberPwd);
        intent.putExtra(HOST, mqttHost);
        intent.putExtra(PORT, mqttPort);
        intent.putExtra(TOPIC_PREFIX, topicPrefix);
        startService(context, intent);
    }

    public static void subscribe(Context context, String... topic) {
        Intent intent = new Intent(context, MQTTService.class);
        intent.setAction(ACTION_SUBSCRIBE_TOPIC);
        intent.putExtra(EXTRA_SUBSCRIBE_TOPIC, topic);
        startService(context, intent);
    }

    public static void unsubscribe(Context context, String... topic) {
        Intent intent = new Intent(context, MQTTService.class);
        intent.setAction(ACTION_UNSUBSCRIBE_TOPIC);
        intent.putExtra(EXTRA_UNSUBSCRIBE_TOPIC, topic);
        startService(context, intent);
    }

    private static void startService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private void startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int nid = 1010;
            String channelId = "MQTTService";
            String channelName = "Background MQTT Service ";
            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            service.createNotificationChannel(chan);

            Notification.Builder builder = new Notification.Builder(this, channelId);
            Notification notification = builder.setOngoing(false)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(nid, notification);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //并且在service里再调用startForeground方法，不然就会出现ANR
            //startForeground(Integer.MAX_VALUE, new Notification());
            startForeground();
        }
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MQTT_INIT.equals(action)) {
                String subscriber = intent.getStringExtra(USER_NAME);
                String subscriberPwd = intent.getStringExtra(PASSWORD);
                String mqttHost = intent.getStringExtra(HOST);
                String mqttPort = intent.getStringExtra(PORT);
                String topicPrefix = intent.getStringExtra(TOPIC_PREFIX);
                MQTTManager.getInstance().init(subscriber, subscriberPwd, mqttHost, mqttPort, topicPrefix);
            } else if (ACTION_SUBSCRIBE_TOPIC.equals(action)) {
                MQTTManager.getInstance().subscribe(intent.getStringArrayExtra(EXTRA_SUBSCRIBE_TOPIC));
            } else if (ACTION_UNSUBSCRIBE_TOPIC.equals(action)) {
                MQTTManager.getInstance().unsubscribe(
                        intent.getStringArrayExtra(EXTRA_UNSUBSCRIBE_TOPIC));
            }
        }
    }
}

package io.tick.base.eventbus;


import org.greenrobot.eventbus.EventBus;

public class EventBusCenter {
    public int code;
    public Object data;

    private EventBusCenter() {
    }

    private EventBusCenter(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    private static EventBusCenter newInstance(int code, Object data) {
        return new EventBusCenter(code, data);
    }

    public static void post(int code) {
        post(code, null);
    }

    public static void post(int code, Object data) {
        EventBus.getDefault().post(newInstance(code, data));
    }
}

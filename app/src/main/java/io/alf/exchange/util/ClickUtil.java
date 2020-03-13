package io.alf.exchange.util;

/**
 * Created by xiaojianjun on 2018/6/8.
 */
public class ClickUtil {
    private static final long interval = 500;
    private static long lastClickTime;

    public static boolean isNotQuickDoubleClick() {
        long nowClickTime = System.currentTimeMillis();
        boolean isNotQuickDoubleClick = nowClickTime - lastClickTime >= interval;
        lastClickTime = nowClickTime;
        return isNotQuickDoubleClick;
    }
}

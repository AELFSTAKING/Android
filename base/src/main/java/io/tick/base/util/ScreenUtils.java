package io.tick.base.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import io.tick.base.ContextProvider;


public class ScreenUtils {

    /**
     * @return return screen width px unit.
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) ContextProvider.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * @return return screen height px unit.
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) ContextProvider.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * Get ActionBar size.
     *
     * @return
     */
    public static int getActionBarSize() {
        int actionBarSize = 0;
        TypedValue tv = new TypedValue();
        if (ContextProvider.getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarSize = TypedValue.complexToDimensionPixelSize(tv.data,
                    ContextProvider.getContext().getResources().getDisplayMetrics());
        }
        return actionBarSize;
    }


    public static float dpToPx(float dp) {
        return dp * ContextProvider.getContext().getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(float px) {
        return px / ContextProvider.getContext().getResources().getDisplayMetrics().density;
    }


    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param number 需要四舍五入的数字
     * @param scale  小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(Double number, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = null == number ? new BigDecimal("0.0") : new BigDecimal(
                Double.toString(number));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 修改状态栏为全透明
     *
     * @param activity
     */
    @TargetApi(19)
    public static void transparencyBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 获取通知栏高度
     *
     * @param context 上下文
     * @return 通知栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object obj = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int temp = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

}

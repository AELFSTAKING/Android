package io.tick.base.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.UUID;

import io.tick.base.BaseApp;

public class DeviceUtils {

    public static String getClientId() {
        String ret = DataPersistenceUtils.getClientId();
        if (TextUtils.isEmpty(ret)) {
            ret = "android_client_" + DeviceUtils.getDeviceID(BaseApp.getContext());
            DataPersistenceUtils.putClientId(ret);
        }
        return ret;
    }

    /**
     * 获取设备信息
     *
     * @return 设备信息
     */
    private static String getDeviceID(Context context) {
        return getUniqueID(context);
    }

    public static String getDeviceInfo(Context context) {
        return "手机型号：" + getPhoneModel() + ";屏幕信息：" + getScreenSizeOfDevice(context) + ";";
//    return "";
    }


    @SuppressLint("HardwareIds")
    private static String getUniqueID(Context context) {

        String telephonyDeviceId = "";
        String androidDeviceId = "";

        // get telephony id
        try {
            final TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                telephonyDeviceId = tm.getDeviceId();
            }
            if (TextUtils.isEmpty(telephonyDeviceId)) {
                telephonyDeviceId = UUID.randomUUID().toString();
            }
        } catch (Exception ignored) {

        }

        // get internal android device id
        try {
            androidDeviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(androidDeviceId)) {
                androidDeviceId = UUID.randomUUID().toString();
            }
        } catch (Exception ignored) {

        }

        // build up the uuid
        try {
            return getStringIntegerHexBlocks(androidDeviceId.hashCode()) + "-"
                    + getStringIntegerHexBlocks(telephonyDeviceId.hashCode());
        } catch (Exception e) {
            return "0000-0000-1111-1111";
        }
    }

    private static String getStringIntegerHexBlocks(int value) {
        StringBuilder result = new StringBuilder();
        String string = Integer.toHexString(value);

        int remain = 8 - string.length();
        char[] chars = new char[remain];
        Arrays.fill(chars, '0');
        string = new String(chars) + string;

        int count = 0;
        for (int i = string.length() - 1; i >= 0; i--) {
            count++;
            result.insert(0, string.substring(i, i + 1));
            if (count == 4) {
                result.insert(0, "-");
                count = 0;
            }
        }
        if (result.toString().startsWith("-")) {
            result = new StringBuilder(result.substring(1, result.length()));
        }
        return result.toString();
    }

    /**
     * 屏幕分辨率
     *
     * @return 高*宽
     */
    private static String getScreenSizeOfDevice(Context context) {
        DisplayMetrics dm =
                context.getApplicationContext().getResources().getDisplayMetrics();
        String height = String.valueOf(dm.heightPixels);
        String width = String.valueOf(dm.widthPixels);
        return height + "*" + width;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    private static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

}

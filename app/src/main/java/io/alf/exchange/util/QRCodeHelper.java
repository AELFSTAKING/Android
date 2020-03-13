package io.alf.exchange.util;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.LogUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import io.alf.exchange.Constant;
import io.tick.base.util.ActivityStartUtils;


public class QRCodeHelper {

    public static void openQRCodeScanner(Activity activity, Class<?> cls) {
        ActivityStartUtils.jumpForResult(activity, cls, Constant.RC_GET_QRCODE);
    }

    public static void openQRCodeScanner(Fragment fragment, Class<?> cls) {
        ActivityStartUtils.jumpForResult(fragment, cls, Constant.RC_GET_QRCODE);
    }

    public static void openQRCodeScanner(Activity activity, Class<?> cls, ActivityStartUtils.IExtras iExtras) {
        ActivityStartUtils.jumpForResult(activity, cls, Constant.RC_GET_QRCODE, iExtras);
    }

    public static void openQRCodeScanner(Fragment fragment, Class<?> cls, ActivityStartUtils.IExtras iExtras) {
        ActivityStartUtils.jumpForResult(fragment, cls, Constant.RC_GET_QRCODE, iExtras);
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data, OnQRCodeGetListener l) {
        if (RESULT_OK == resultCode && null != data) {
            switch (requestCode) {
                case Constant.RC_GET_QRCODE:
                    if (null != l) {
                        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
                        String content = result.getContents();
                        LogUtils.i(" QRCode: " + content);
                        if (null != content) {
                            l.onGetQRCode(content);
                        }
                    }
                    break;
            }
        }
    }

    public interface OnQRCodeGetListener {
        void onGetQRCode(String result);
    }
}

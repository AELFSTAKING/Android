package io.alf.exchange.util;

import android.os.Environment;

import java.io.File;

import io.alf.exchange.App;

/**
 * "/Android/data/" + context.getPackageName() + "/cache/"
 */

public class PathUtils {
    public static final String IMAGE_DIR = "images";//图片存储目录
    public static final String DOWNLOAD_DIR = "downloads";//apk版本更新存放目录
    public static final String HTTP_DIR = "http";//http请求
    public static final String H5_ASSET_PREFIX = "file:///android_asset/";
    public static final String H5_SDCARD_PREFIX = "content://com.android.htmlfileprovider/";

    public static File getAppCacheDir() {
        File dir = isExistSDCard() ? getExternalCacheDir() : getInnerCacheDir();
        if (null != dir && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getAppFilesDir() {
        File dir = isExistSDCard() ? getExternalFilesDir() : getInnerFilesDir();
        if (null != dir && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    /**
     * sd card
     */
    public static File getExternalCacheDir() {
        return App.getContext().getExternalCacheDir();
        //return new File(Environment.getExternalStorageDirectory(), "/Android/data/" + BaseApp.getInstance().getPackageName() + "/cache/");
    }

    /**
     * RAM
     */
    public static File getInnerCacheDir() {
        return App.getContext().getCacheDir();
    }

    public static File getExternalFilesDir() {
        return App.getContext().getExternalFilesDir(null);
        //return new File(Environment.getExternalStorageDirectory(), "/Android/data/" + BaseApp.getInstance().getPackageName() + "/files/");
    }

    public static File getInnerFilesDir() {
        return App.getContext().getFilesDir();
    }


    public static boolean isExistSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}

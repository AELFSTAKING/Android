package io.tick.base.appupdate;

import android.app.Activity;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.vector.update_app.UpdateAppManager;

import java.util.HashMap;
import java.util.Map;


public class AppUpdateUtils {

    private static long lastUpdateTime = 0;
    private static String ignoreVersion = "";

    public static void putIgnoreVersion(String version) {
        ignoreVersion = version;
    }

    public static String getIgnoreVersion() {
        return ignoreVersion;
    }

    public static void checkUpdate(Activity activity, String updateUrl) {
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime == 0 || (currentTime - lastUpdateTime) > (1000)) {
            lastUpdateTime = currentTime;
            new UpdateAppManager
                    .Builder()
                    //当前Activity
                    .setActivity(activity)
                    //更新地址
                    .setUpdateUrl(updateUrl)
                    //全局异常捕获
                    .handleException(e -> {
                        CrashReport.postCatchedException(e);
                        e.printStackTrace();
                    })
                    //实现httpManager接口的对象
                    .setHttpManager(new UpdateManager())
                    .setPost(true)
                    .setParams(getParams(activity))
                    .setUpdateDialogFragmentListener(updateApp -> {
                        //用户点击关闭按钮，取消了更新，如果是下载完，用户取消了安装，则可以在 onActivityResult 监听到。
                        if (updateApp != null && !StringUtils.isEmpty(
                                updateApp.getNewVersion())) {
                            putIgnoreVersion(updateApp.getNewVersion());
                            Log.i("Tick", "ignore : " + updateApp.getNewVersion());
                        }
                    })
                    .build()
                    .checkNewApp(new MUpdateCallback());
        }
    }

    private static Map<String, String> getParams(Activity activity) {
        String versionName = getVersionName(activity);
        Map<String, String> params = new HashMap<>();
        params.put("os", "2");
        params.put("version", versionName);
        //params.put("app", "otc");
        return params;
    }

    private static String getVersionName(Activity activity) {
        return com.vector.update_app.utils.AppUpdateUtils.getVersionName(activity);
    }
}

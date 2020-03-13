package io.tick.base.appupdate;

import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.vector.update_app.service.DownloadService;

import org.json.JSONObject;

import java.io.File;

/**
 * 新版本版本检测回调
 */
public class MUpdateCallback extends UpdateCallback {

    /**
     * 解析json,自定义协议
     *
     * @param json 服务器返回的json
     * @return UpdateAppBean
     */
    protected UpdateAppBean parseJson(String json) {
        UpdateAppBean updateAppBean = new UpdateAppBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            updateAppBean.setUpdate(jsonObject.optBoolean("update"))
                    .setForcedUpdate(jsonObject.optBoolean("forcedUpdate"))
                    .setNewVersion(jsonObject.optString("newVersion"))
                    .setDownloadUrl(jsonObject.optString("downloadUrl"))
                    .setSummary(jsonObject.optString("summary"))
                    .setFileSize(jsonObject.optString("fileSize"))
                    .setMd5(jsonObject.optString("md5"))
                    //存放json，方便自定义解析
                    .setOriginRes(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateAppBean;
    }

    /**
     * 有新版本
     *
     * @param updateApp        新版本信息
     * @param updateAppManager app更新管理器
     */
    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
        // 忽略用户取消的版本
        if (!StringUtils.isEmpty(AppUpdateUtils.getIgnoreVersion())
                && StringUtils.equals(updateApp.getNewVersion(),
                AppUpdateUtils.getIgnoreVersion())) {
            return;
        }
        if (updateApp.isForcedUpdate()) {
            // 1.强制更新
            updateAppManager.showDialogFragment();
        } else {
            // 2.静默更新
            // 添加信息
            UpdateAppBean updateAppBean = updateAppManager.fillUpdateAppData();
            // 如果新版本已经下载成功, 则弹窗让用户安装。
            if (com.vector.update_app.utils.AppUpdateUtils.appIsDownloaded(updateApp)) {
                updateAppManager.showDialogFragment();
            } else {
                //如果不是Wifi环境则弹出更新对话框让用户选择是否下载，否则静默下载。
                if (!com.vector.update_app.utils.AppUpdateUtils.isWifi(
                        updateAppManager.getContext())) {
                    updateAppManager.showDialogFragment();
                } else {
                    // 设置不显示通知栏下载进度
                    updateAppBean.dismissNotificationProgress(true);
                    updateAppManager.download(new DownloadService.DownloadCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onProgress(float progress, long totalSize) {

                        }

                        @Override
                        public void setMax(long totalSize) {

                        }

                        @Override
                        public boolean onFinish(File file) {
                            // 如果新版本已经下载成功, 则弹窗让用户安装。
                            updateAppManager.showDialogFragment();
                            return false;
                        }


                        @Override
                        public void onError(String msg) {

                        }

                        @Override
                        public boolean onInstallAppAndAppOnForeground(File file) {
                            return false;
                        }
                    });
                }
            }
        }
        Log.i("Tick", "hasNewApp : " + updateApp.getNewVersion());
    }

    /**
     * 网路请求之后
     */
    protected void onAfter() {
    }


    /**
     * 没有新版本
     *
     * @param error HttpManager实现类请求出错返回的错误消息，交给使用者自己返回，有可能不同的应用错误内容需要提示给客户
     */
    protected void noNewApp(String error) {
    }

    /**
     * 网络请求之前
     */
    protected void onBefore() {
    }

}

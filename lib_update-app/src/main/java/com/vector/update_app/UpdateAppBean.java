package com.vector.update_app;

import java.io.Serializable;

/**
 * 版本信息
 */
public class UpdateAppBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * update : true
     * newVersion : xxxxx
     * downloadUrl : http://cdn.the.url.of.apk/or/patch
     * updateLog : xxxx
     * delta : false
     * md5 : xxxxxxxxxxxxxx
     * fileSize : 601132
     */
    //是否有新版本
    private boolean update;
    //是否强制更新
    private boolean forcedUpdate;
    //新版本号
    private String newVersion;
    //新app下载地址
    private String downloadUrl;
    //更新日志
    private String summary;
    //新app大小
    private String fileSize;
    //md5
    private String md5;
    //配置默认更新dialog 的title
    private String update_def_dialog_title;
    //是否增量 暂时不用
    private boolean delta;
    //服务器端的原生返回数据（json）,方便使用者在hasNewApp自定义渲染dialog的时候可以有别的控制，比如：#issues/59
    private String origin_res;
    /**********以下是内部使用的数据**********/

    //网络工具，内部使用
    private HttpManager httpManager;
    private String targetPath;
    private boolean mHideDialog;
    private boolean mShowIgnoreVersion;
    private boolean mDismissNotificationProgress;
    private boolean mOnlyWifi;

    //是否隐藏对话框下载进度条,内部使用
    public boolean isHideDialog() {
        return mHideDialog;
    }

    public void setHideDialog(boolean hideDialog) {
        mHideDialog = hideDialog;
    }

    public boolean isUpdate() {
        return update;
    }

    public HttpManager getHttpManager() {
        return httpManager;
    }

    public void setHttpManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public boolean isForcedUpdate() {
        return forcedUpdate;
    }

    public UpdateAppBean setForcedUpdate(boolean forcedUpdate) {
        this.forcedUpdate = forcedUpdate;
        return this;
    }

    public boolean getUpdate() {
        return update;
    }

    public UpdateAppBean setUpdate(boolean update) {
        this.update = update;
        return this;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public UpdateAppBean setNewVersion(String newVersion) {
        this.newVersion = newVersion;
        return this;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }


    public UpdateAppBean setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public UpdateAppBean setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getUpdateDefDialogTitle() {
        return update_def_dialog_title;
    }

    public UpdateAppBean setUpdateDefDialogTitle(String updateDefDialogTitle) {
        this.update_def_dialog_title = updateDefDialogTitle;
        return this;
    }

    public boolean isDelta() {
        return delta;
    }

    public void setDelta(boolean delta) {
        this.delta = delta;
    }

    public String getMd5() {
        return md5;
    }

    public UpdateAppBean setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getFileSize() {
        return fileSize;
    }

    public UpdateAppBean setFileSize(String fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public boolean isShowIgnoreVersion() {
        return mShowIgnoreVersion;
    }

    public void showIgnoreVersion(boolean showIgnoreVersion) {
        mShowIgnoreVersion = showIgnoreVersion;
    }

    public void dismissNotificationProgress(boolean dismissNotificationProgress) {
        mDismissNotificationProgress = dismissNotificationProgress;
    }

    public boolean isDismissNotificationProgress() {
        return mDismissNotificationProgress;
    }

    public boolean isOnlyWifi() {
        return mOnlyWifi;
    }

    public void setOnlyWifi(boolean onlyWifi) {
        mOnlyWifi = onlyWifi;
    }

    public String getOriginRes() {
        return origin_res;
    }

    public UpdateAppBean setOriginRes(String originRes) {
        this.origin_res = originRes;
        return this;
    }

}

package io.tick.base.appupdate;

public class AppUpdateBean {

    /**
     * downloadUrl : http://k-upexcahnge.oss-cn-beijing.aliyuncs.com/uat/android/UpExchange_1.0
     * .3_release_201901190236.apk
     * forcedUpdate : true
     * md5 :
     * newVersion : 1.0.3
     * summary : 强制更新
     * update : true
     */

    private String downloadUrl;
    private boolean forcedUpdate;
    private String md5;
    private String newVersion;
    private String summary;
    private boolean update;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isForcedUpdate() {
        return forcedUpdate;
    }

    public void setForcedUpdate(boolean forcedUpdate) {
        this.forcedUpdate = forcedUpdate;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}

package io.tick.base.download;

/**
 * 下载监听
 *
 * @author Cheny
 */
public interface DownloadListener {
    void onFinished();

    void onProgress(float progress);

    void onPause();

    void onCancel();
}

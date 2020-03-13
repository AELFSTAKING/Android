package io.alf.exchange.mvp.bean;

import java.io.Serializable;

public class BannerBean implements Serializable {
    // 业务id
    public String bizId;
    // 图片内容链接
    public String imageUrl;
    // 图片点击链接目的
    public String link;

    public int localImgResId;

    public BannerBean(String bizId, String imageUrl, String link, int localImgResId) {
        this.bizId = bizId;
        this.imageUrl = imageUrl;
        this.link = link;
        this.localImgResId = localImgResId;
    }
}

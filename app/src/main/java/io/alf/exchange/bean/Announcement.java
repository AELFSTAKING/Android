package io.alf.exchange.bean;

import java.io.Serializable;

public class Announcement implements Serializable {
    // 业务id
    public String bizId;
    // 标题
    public String title;
    // 发布时间
    public long publishTime;

    public Announcement(String bizId, String title, long publishTime) {
        this.bizId = bizId;
        this.title = title;
        this.publishTime = publishTime;
    }
}

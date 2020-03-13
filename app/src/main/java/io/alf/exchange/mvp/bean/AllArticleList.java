package io.alf.exchange.mvp.bean;

import java.io.Serializable;
import java.util.List;

import io.alf.exchange.bean.Announcement;

public class AllArticleList implements Serializable {
    //banner列表
    private List<BannerBean> bannerList;
    //滚动公告列表
    private List<Announcement> announcementList;
    //行业资讯列表
    private List<NewsBean> newsList;

    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerBean> bannerList) {
        this.bannerList = bannerList;
    }

    public List<Announcement> getAnnouncementList() {
        return announcementList;
    }

    public void setAnnouncementList(List<Announcement> announcementList) {
        this.announcementList = announcementList;
    }

    public List<NewsBean> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<NewsBean> newsList) {
        this.newsList = newsList;
    }

    public static class NewsBean implements Serializable {
        // 业务id
        private String bizId;
        // 标题
        private String title;
        // 新闻简介
        private String summary;
        // 发布时间
        private long publishTime;

        public String getBizId() {
            return bizId;
        }

        public void setBizId(String bizId) {
            this.bizId = bizId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public long getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(long publishTime) {
            this.publishTime = publishTime;
        }

    }
}

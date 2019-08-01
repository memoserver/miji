package com.mijimemo.server.entity;

import java.util.Date;
import java.util.List;

public class Private {

    private Long privateId;

    private Long userId;

    private User user;

    private String title;

    private String content;

    private Date publishTime;

    private Date alarmTime;

    private int urgent;

    private List<String> images;

    private List<String> tags;

    public Private() {
    }

    public Private(Long privateId, Long userId, String title, String content, Date publishTime, Date alarmTime, int urgent, List<String> images, List<String> tags) {
        this.privateId = privateId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.publishTime = publishTime;
        this.alarmTime = alarmTime;
        this.urgent = urgent;
        this.images = images;
        this.tags = tags;
    }

    public Long getPrivateId() {
        return privateId;
    }

    public void setPrivateId(Long privateId) {
        this.privateId = privateId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getUrgent() {
        return urgent;
    }

    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

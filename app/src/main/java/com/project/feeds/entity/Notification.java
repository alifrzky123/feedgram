package com.project.feeds.entity;

public class Notification {
    String userId;
    String text;
    Boolean isPost;
    String idUploader;

    public Notification (){}
    public Notification(String userId, String text, Boolean isPost, String idUploader) {
        this.userId = userId;
        this.text = text;
        this.isPost = isPost;
        this.idUploader = idUploader;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public Boolean getPost() {
        return isPost;
    }

    public String getIdUploader() {
        return idUploader;
    }
}

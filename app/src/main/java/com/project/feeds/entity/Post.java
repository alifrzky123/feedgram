package com.project.feeds.entity;

public class Post {
    String uploadId,postImages,postUploader,postDesc;

    public Post(String uploadId, String postImages, String postUploader, String postDesc) {
        this.uploadId = uploadId;
        this.postImages = postImages;
        this.postUploader = postUploader;
        this.postDesc = postDesc;
    }
    public Post(){

    }

    public String getUploadId() {
        return uploadId;
    }

    public String getPostImages() {
        return postImages;
    }

    public String getPostUploader() {
        return postUploader;
    }

    public String getPostDesc() {
        return postDesc;
    }
}

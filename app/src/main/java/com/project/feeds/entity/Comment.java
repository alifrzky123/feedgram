package com.project.feeds.entity;

public class Comment {
    String text, mrComment;

    public String getMrComment() {
        return mrComment;
    }

    public Comment(String text, String mrComment) {
        this.text = text;
        this.mrComment = mrComment;
    }

    public Comment(){

    }
    public String getText() {
        return text;
    }

}

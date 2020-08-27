package com.project.feeds.entity;

public class User {
    private String id;
    private String userName;
    private String fullName;
    private String email;
    private String bio;
    private String photoUrl;
    private String password;

    public User(){

    }

    public User(String id, String userName, String email, String bio, String photoUrl, String password, String fullName) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.email = email;
        this.bio = bio;
        this.photoUrl = photoUrl;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPassword() {
        return password;
    }
}

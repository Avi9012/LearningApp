package com.example.gangu.chatapp.model;

public class User {

    private String id;
    private String username;
    private String imageUrl;
    private String status;
    private String Emailverify;

    public User(String id, String username, String imageUrl, String status, String Emailverify) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
        this.status = status;
        this.Emailverify = Emailverify;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmailverify() {
        return Emailverify;
    }

    public void setEmailverify(String emailverify) {
        Emailverify = emailverify;
    }
}

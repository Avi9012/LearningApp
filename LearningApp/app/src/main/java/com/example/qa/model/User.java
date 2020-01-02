package com.example.qa.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String UserId, Email;
    private String name;
    private String imageUrl;
    private String status;
    private String isAdmin;
    private List<String> categories = new ArrayList<>();
    private String emailverify;

    public User(String UserId, String name, String imageUrl, String status, String isAdmin, List<String> categories, String emailverify, String Email) {
        this.UserId = UserId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.status = status;
        this.isAdmin = isAdmin;
        this.categories = categories;
        this.emailverify = emailverify;
        this.Email = Email;
    }

    public User() {

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getEmailverify() {
        return emailverify;
    }

    public void setEmailverify(String emailverify) {
        this.emailverify = emailverify;
    }
}

package com.example.qa;

public class Answers {

    private String QueId, time, date, id, string, UserId;

    public Answers() {
    }

    public Answers(String QueId, String time, String date, String id, String string, String UserId) {
        this.QueId = QueId;
        this.time = time;
        this.date = date;
        this.id = id;
        this.UserId = UserId;
        this.string = string;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getQueId() {
        return QueId;
    }

    public void setQueId(String QueId) {
        this.QueId = QueId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}

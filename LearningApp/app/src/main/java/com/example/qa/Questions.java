package com.example.qa;

public class Questions {

    private String string, Time, Date, Id, UserId;
    Long Answers;

    public Questions() {
    }

    public Questions(String string, String Time, String Date, Long Answers, String Id, String UserId) {
        this.string = string;
        this.Time = Time;
        this.Date = Date;
        this.Answers = Answers;
        this.UserId = UserId;
        this.Id = Id;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getTime() {

        return Time;
    }

    public void setTime(String Time) {

        this.Time = Time;
    }

    public String getDate() {

        return Date;
    }

    public void setDate(String Date) {

        this.Date = Date;
    }

    public Long getAnswers() {

        return Answers;
    }

    public void setAnswers(Long Answers) {

        this.Answers = Answers;
    }
}
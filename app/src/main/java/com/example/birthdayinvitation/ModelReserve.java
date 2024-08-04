package com.example.birthdayinvitation;

public class ModelReserve {

    //user same spellings of variable as used in sending to firebase
    String uid, name, phone, attendees, feedback, timestamp;

    public ModelReserve() {
    }

    public ModelReserve(String uid, String name, String phone, String attendees, String feedback, String timestamp) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.feedback = feedback;
        this.attendees = attendees;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

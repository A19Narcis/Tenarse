package com.example.tenarse.ui.active_chat;

public class MyPostObject {
    String id;
    String url_img;
    String profile_img;
    String userName;
    String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl_img() {
        return url_img;
    }

    public void setUrl_img(String url_img) {
        this.url_img = url_img;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MyPostObject(String id, String url_img, String profile_img, String userName, String message) {
        this.id = id;
        this.url_img = url_img;
        this.profile_img = profile_img;
        this.userName = userName;
        this.message = message;
    }
}


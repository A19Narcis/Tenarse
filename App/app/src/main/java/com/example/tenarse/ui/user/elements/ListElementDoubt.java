package com.example.tenarse.ui.user.elements;

public class ListElementDoubt {
    public String user_img_url;
    public String username;
    public String title;
    public String description;
    public String doubt_id;

    public ListElementDoubt(String username, String title, String descr, String doubt_id, String user_img_url){
        this.username = username;
        this.title = title;
        this.description = descr;
        this.doubt_id = doubt_id;
        this.user_img_url = user_img_url;
    }

    public String getDoubt_id() {
        return doubt_id;
    }

    public void setDoubt_id(String doubt_id) {
        this.doubt_id = doubt_id;
    }

    public String getUser_img_url() {
        return user_img_url;
    }

    public void setUser_img_url(String user_img_url) {
        this.user_img_url = user_img_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


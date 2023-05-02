package com.example.tenarse.ui.search.posts;

import java.io.Serializable;

public class ListElementImg implements Serializable {
    public String user_img_url;
    public String username;
    public String post_img_url;
    public String post_img_text;
    public String post_img_id;

    public ListElementImg(String username, String text, String post_url, String post_img_id){
        this.username = username;
        this.post_img_text = text;
        this.post_img_url = post_url;
        this.post_img_id = post_img_id;
    }

    public String getPost_img_id() {
        return post_img_id;
    }

    public void setPost_img_id(String post_img_id) {
        this.post_img_id = post_img_id;
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

    public String getPost_img_url() {
        return post_img_url;
    }

    public void setPost_img_url(String post_img_url) {
        this.post_img_url = post_img_url;
    }

    public String getPost_img_text() {
        return post_img_text;
    }

    public void setPost_img_text(String post_img_text) {
        this.post_img_text = post_img_text;
    }
}
